package com.vsnt.services;

import com.vsnt.SegmentEventFactory;
import com.vsnt.SegmentEventProducer;
import com.vsnt.common_lib.dtos.events.asset.transcoding.AssetTranscodingProgressEvent;
import com.vsnt.common_lib.dtos.events.asset.transcoding.AssetTranscodingProgressPayload;
import com.vsnt.dtos.MediaType;
import com.vsnt.dtos.TranscodingSegmentUpdateDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static java.nio.file.StandardWatchEventKinds.*;

public class HlsDirectoryWatcher implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(HlsDirectoryWatcher.class);

    private static final int    MAX_RETRIES            = 3;
    private static final long   RETRY_BACKOFF_MS       = 500L;
    private static final long   WATCH_POLL_TIMEOUT_SEC = 2L;
    private static final long   EXECUTOR_SHUTDOWN_SEC  = 30L;

    private static final List<String> VARIANTS         = List.of("0", "1", "2", "3");

    private final SegmentEventFactory  segmentEventFactory;
    private final SegmentEventProducer producer;
    private final WatchService         watchService;
    private final ExecutorService      executor;

    private final ConcurrentHashMap<WatchKey, Path> keyDirectoryMap = new ConcurrentHashMap<>();
    private final AtomicInteger completedSegments = new AtomicInteger(0);

    private final String assetId;
    private final String mediaId;


    private final CompletableFuture<Void> completionFuture = new CompletableFuture<>();
    private volatile boolean running = false;

    public HlsDirectoryWatcher(
            String basePath,
            SegmentEventFactory segmentEventFactory,
            SegmentEventProducer producer,
            String assetId,
            String mediaId,
            int totalSegments
    ) throws IOException {
        this.segmentEventFactory = segmentEventFactory;
        this.producer            = producer;
        this.assetId             = assetId;
        this.mediaId             = mediaId;

        this.watchService        = FileSystems.getDefault().newWatchService();
        this.executor            = buildExecutor();

        for (String variant : VARIANTS) {
            registerDirectory(Paths.get(basePath, variant));
        }

        logger.info("HLS watcher initialized. mediaId={}, assetId={}, totalSegments={}",
                mediaId, assetId, totalSegments);
    }

    public CompletableFuture<Void> getCompletionFuture() {
        return completionFuture;
    }

    public synchronized void start() {
        if (running) {
            logger.warn("Watcher already running. mediaId={}", mediaId);
            return;
        }
        running = true;
        Thread t = new Thread(this, "hls-directory-watcher-" + mediaId);
        t.start();
        logger.info("Watcher started. mediaId={}, thread={}", mediaId, t.getName());
    }

    public synchronized void stop() {
        logger.warn("Stopping watcher. mediaId={}", mediaId);
        running = false;
        closeWatchService();
    }

    @Override
    public void run() {
        logger.info("Watcher loop started. mediaId={}", mediaId);
        try {
            watchLoop();
        } finally {
            shutdown();
        }
    }

    private void watchLoop() {
        while (running) {
            WatchKey key = pollKey();
            if (key == null) continue;

            Path dir = keyDirectoryMap.get(key);
            if (dir == null) {
                key.reset();
                continue;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                handleEvent(event, dir);
            }

            key.reset();
        }
    }

    private WatchKey pollKey() {
        try {
            return watchService.poll(WATCH_POLL_TIMEOUT_SEC, TimeUnit.SECONDS);
        } catch (InterruptedException | ClosedWatchServiceException e) {
            logger.warn("Watcher interrupted or closed. mediaId={}", mediaId);
            running = false;
            return null;
        }
    }

    private void handleEvent(WatchEvent<?> event, Path dir) {
        if (event.kind() == OVERFLOW) {
            logger.warn("WatchService overflow — some events may have been lost. mediaId={}", mediaId);
            return;
        }

        Path fullPath = dir.resolve((Path) event.context());
        String filename = fullPath.toString();

        if (filename.endsWith(".tmp") || !filename.endsWith(".ts")) return;

        processSegment(fullPath);
    }

    private void processSegment(Path fullPath) {
        executor.submit(() -> processWithRetry(fullPath));
    }

    private void processWithRetry(Path fullPath) {
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                logger.info("Processing segment {}", fullPath);
                TranscodingSegmentUpdateDTO update =
                        segmentEventFactory.generate(fullPath, assetId, mediaId, MediaType.STATIC);
                logger.info("Transcoding segment update: {}", update);
                if (update == null) throw new RuntimeException("Segment factory returned null");

                producer.sendEvent(update);
                Files.deleteIfExists(fullPath);


                return;

            } catch (Exception e) {
                logger.warn("Segment processing failed. attempt={}/{}, file={}, mediaId={}",
                        attempt, MAX_RETRIES, fullPath, mediaId, e);

                if (attempt < MAX_RETRIES) sleepBackoff(attempt);
            }
        }
        logger.error("Segment permanently failed after {} retries. file={}, mediaId={}",
                MAX_RETRIES, fullPath, mediaId);
    }



    private void drainRemainingFiles() {
        logger.info("Draining remaining segments. mediaId={}", mediaId);
        for (Path dir : keyDirectoryMap.values()) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.ts")) {
                for (Path file : stream) {
                    processSegment(file);
                }
            } catch (IOException e) {
                logger.error("Drain failed. dir={}, mediaId={}", dir, mediaId, e);
            }
        }
    }

    private void shutdown() {
        drainRemainingFiles();
        executor.shutdown();
        try {
            if (!executor.awaitTermination(EXECUTOR_SHUTDOWN_SEC, TimeUnit.SECONDS)) {
                logger.warn("Executor did not terminate cleanly. mediaId={}", mediaId);
            }
        } catch (InterruptedException e) {
            logger.error("Interrupted during executor shutdown. mediaId={}", mediaId);
            Thread.currentThread().interrupt();
        }
        completionFuture.complete(null);
        logger.info("Watcher stopped cleanly. mediaId={}", mediaId);
    }

    private void registerDirectory(Path dir) throws IOException {
        WatchKey key = dir.register(watchService, ENTRY_CREATE);
        keyDirectoryMap.put(key, dir);
        logger.info("Watching directory: {}", dir);
    }

    private void closeWatchService() {
        try {
            watchService.close();
        } catch (IOException e) {
            logger.error("Error closing WatchService. mediaId={}", mediaId, e);
        }
    }

    private void sleepBackoff(int attempt) {
        try {
            Thread.sleep(RETRY_BACKOFF_MS * attempt);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

    private static ExecutorService buildExecutor() {
        return new ThreadPoolExecutor(
                4, 8,
                60, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(1000),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }
}