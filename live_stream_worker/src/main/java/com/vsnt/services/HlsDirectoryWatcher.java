package com.vsnt.services;

import com.vsnt.SegmentEventFactory;
import com.vsnt.SegmentEventProducer;
import com.vsnt.dtos.MediaType;
import com.vsnt.dtos.TranscodingSegmentUpdateDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
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

    private final int totalVariants = 4;
    private final ExecutorService executor;
    private final CompletableFuture<Void> completionFuture = new CompletableFuture<>();

    private volatile boolean running = false;
    private Thread watcherThread;

    public HlsDirectoryWatcher(String basePath,
                               SegmentEventFactory segmentEventFactory,
                               SegmentEventProducer producer,
                               String assetId,
                               String mediaId) throws IOException {

        logger.info("Initializing HLS Directory Watcher. basePath={}, assetId={}, mediaId={}",
                basePath, assetId, mediaId);

        this.segmentEventFactory = segmentEventFactory;
        this.producer = producer;
        this.assetId = assetId;
        this.mediaId = mediaId;

        this.watchService = FileSystems.getDefault().newWatchService();
        logger.info("WatchService created");

        this.executor = new ThreadPoolExecutor(
                4, 8,
                60, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(1000),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
        logger.info("Executor created with bounded queue");

        String[] variants = {"0", "1", "2", "3"};
        for (String variant : variants) {
            Path dir = Paths.get(basePath, variant);
            registerDirectory(dir);
        }

        logger.info("All variant directories registered for watching");
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
            logger.warn("Watcher already running");
            return;
        }

        running = true;
        watcherThread = new Thread(this);
        watcherThread.setName("hls-directory-watcher");
        watcherThread.start();

        logger.info("Watcher started. thread={}", watcherThread.getName());
    }

    public synchronized void stop() {
        logger.warn("Stopping HLS Directory Watcher...");

        running = false;

        try {
            watchService.close();
        } catch (IOException e) {
            logger.error("Error closing WatchService", e);
        }

        executor.shutdown();
    }

    private void processSegment(Path fullPath) {
        executor.submit(() -> {
            try {
                if (fullPath.toString().endsWith(".tmp")) {
                    logger.debug("Ignoring temp file: {}", fullPath);
                    return;
                }

                if (!fullPath.toString().endsWith(".ts")) {
                    logger.debug("Ignoring non-ts file: {}", fullPath);
                    return;
                }

                logger.info("Processing segment: {}", fullPath);

                TranscodingSegmentUpdateDTO update =
                        segmentEventFactory.generate(fullPath, assetId, mediaId, MediaType.LIVE);

                producer.sendEvent(update);

                Files.deleteIfExists(fullPath);

                int count = completedSegments.incrementAndGet();
                logger.debug("Segment processed and deleted. totalProcessed={}", count);

            } catch (Exception e) {
                logger.error("Failed to process segment: {}", fullPath, e);
            }
        });
    }

    private void drainRemainingFiles() {
        logger.info("Draining remaining segments before shutdown...");

        for (Path dir : keyDirectoryMap.values()) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.ts")) {
                for (Path file : stream) {
                    processSegment(file);
                }
            } catch (IOException e) {
                logger.error("Drain failed for directory: {}", dir, e);
            }
        }
    }

    @Override
    public void run() {
        logger.info("Watcher loop started. thread={}", Thread.currentThread().getName());

        try {
            while (running) {
                WatchKey key;
                try {
                    key = watchService.poll(2, TimeUnit.SECONDS);
                    if (key == null) continue;

                } catch (InterruptedException | ClosedWatchServiceException e) {
                    logger.warn("Watcher interrupted or closed");
                    break;
                }

                Path dir = keyDirectoryMap.get(key);
                if (dir == null) {
                    logger.warn("WatchKey not mapped to directory");
                    continue;
                }

                for (WatchEvent<?> event : key.pollEvents()) {
                    if (event.kind() == OVERFLOW) {
                        logger.warn("WatchService overflow event");
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
                if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                    logger.warn("Executor did not terminate in time");
                }
            } catch (InterruptedException e) {
                logger.error("Interrupted during executor shutdown", e);
            }

            completionFuture.complete(null);

            logger.info("Watcher stopped cleanly");
        }
    }
}