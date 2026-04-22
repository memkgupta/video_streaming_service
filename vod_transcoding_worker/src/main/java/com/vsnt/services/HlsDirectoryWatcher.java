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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

public class HlsDirectoryWatcher implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(HlsDirectoryWatcher.class);

    private final WatchService watchService;
    private final Map<WatchKey, Path> keyDirectoryMap = new HashMap<>();
    private final SegmentEventFactory segmentEventFactory;
    private final SegmentEventProducer producer;
    private final String assetId;
    private final String mediaId;

    private final ConcurrentHashMap<String, AtomicInteger> segmentTracker = new ConcurrentHashMap<>();
    private final AtomicInteger completedSegments = new AtomicInteger(0);

    private final int totalSegments;
    private final int totalVariants = 4;

    private final ExecutorService executor;
    private final CompletableFuture<Void> completionFuture = new CompletableFuture<>();

    private volatile boolean running = false;
    private Thread watcherThread;

    public HlsDirectoryWatcher(String basePath,
                               SegmentEventFactory segmentEventFactory,
                               SegmentEventProducer producer,
                               String assetId,
                               String mediaId,
                               int totalSegments) throws IOException {

        this.segmentEventFactory = segmentEventFactory;
        this.producer = producer;
        this.assetId = assetId;
        this.mediaId = mediaId;
        this.totalSegments = totalSegments;

        this.watchService = FileSystems.getDefault().newWatchService();

        this.executor = new ThreadPoolExecutor(
                4, 8,
                60, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(1000),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );

        String[] variants = {"0", "1", "2", "3"};
        for (String variant : variants) {
            Path dir = Paths.get(basePath, variant);
            registerDirectory(dir);
        }

        logger.info("HLS watcher initialized. mediaId={}, assetId={}, totalSegments={}",
                mediaId, assetId, totalSegments);
    }

    public CompletableFuture<Void> getCompletionFuture() {
        return completionFuture;
    }

    private void registerDirectory(Path dir) throws IOException {
        WatchKey key = dir.register(watchService, ENTRY_CREATE);
        keyDirectoryMap.put(key, dir);
        logger.info("Watching directory: {}", dir);
    }

    public synchronized void start() {
        if (running) {
            logger.warn("Watcher already running. mediaId={}", mediaId);
            return;
        }

        running = true;
        watcherThread = new Thread(this);
        watcherThread.setName("hls-directory-watcher");
        watcherThread.start();

        logger.info("Watcher started. mediaId={}, thread={}", mediaId, watcherThread.getName());
    }

    public synchronized void stop() {
        logger.warn("Stopping watcher. mediaId={}", mediaId);

        running = false;

        try {
            watchService.close();
        } catch (IOException e) {
            logger.error("Error closing watch service", e);
        }

        executor.shutdown();
    }

    private void processSegment(Path fullPath) {
        executor.submit(() -> {
            int attempt = 0;
            int maxRetries = 3;

            while (attempt < maxRetries) {
                try {
                    if (fullPath.toString().endsWith(".tmp")) return;
                    if (!fullPath.toString().endsWith(".ts")) return;

                    logger.debug("Processing segment. mediaId={}, file={}", mediaId, fullPath);

                    TranscodingSegmentUpdateDTO update =
                            segmentEventFactory.generate(fullPath, assetId, mediaId, MediaType.STATIC);

                    if (update == null) {
                        throw new RuntimeException("Segment factory returned null");
                    }

                    producer.sendEvent(update);

                    Files.deleteIfExists(fullPath);

                    int done = completedSegments.incrementAndGet();

                    if (done % 50 == 0) {
                        double progress = ((double) done / totalSegments) * 100;
                        logger.info("Progress {}% ({}/{}) mediaId={}", progress, done, totalSegments, mediaId);

                        producer.sendProgress(new AssetTranscodingProgressEvent(
                                assetId,
                                Instant.now(),
                                new AssetTranscodingProgressPayload(progress)
                        ));
                    }

                    return; // success

                } catch (Exception e) {
                    attempt++;
                    logger.warn("Segment processing failed. attempt={}, file={}, mediaId={}",
                            attempt, fullPath, mediaId, e);

                    try {
                        Thread.sleep(500L * attempt);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }

            logger.error("Segment permanently failed after retries. file={}, mediaId={}",
                    fullPath, mediaId);
        });
    }

    private void drainRemainingFiles() {
        logger.info("Draining remaining segments. mediaId={}", mediaId);

        for (Path dir : keyDirectoryMap.values()) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.ts")) {
                for (Path file : stream) {
                    processSegment(file);
                }
            } catch (IOException e) {
                logger.error("Drain failed for dir={}", dir, e);
            }
        }
    }

    @Override
    public void run() {
        logger.info("Watcher loop started. mediaId={}", mediaId);

        try {
            while (running) {
                WatchKey key;

                try {
                    key = watchService.poll(2, TimeUnit.SECONDS);
                    if (key == null) continue;

                } catch (InterruptedException | ClosedWatchServiceException e) {
                    logger.warn("Watcher interrupted/closed. mediaId={}", mediaId);
                    break;
                }

                Path dir = keyDirectoryMap.get(key);
                if (dir == null) continue;

                for (WatchEvent<?> event : key.pollEvents()) {
                    if (event.kind() == OVERFLOW) {
                        logger.warn("Watch overflow. mediaId={}", mediaId);
                        continue;
                    }

                    Path fullPath = dir.resolve((Path) event.context());
                    processSegment(fullPath);
                }

                key.reset();
            }

        } finally {
            drainRemainingFiles();

            executor.shutdown();
            try {
                executor.awaitTermination(30, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                logger.error("Interrupted during shutdown", e);
            }

            completionFuture.complete(null);

            logger.info("Watcher stopped cleanly. mediaId={}", mediaId);
        }
    }
}