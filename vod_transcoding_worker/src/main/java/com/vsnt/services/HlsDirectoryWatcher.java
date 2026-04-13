package com.vsnt.services;


import com.vsnt.SegmentEventFactory;
import com.vsnt.SegmentEventProducer;
import com.vsnt.dtos.MediaType;
import com.vsnt.dtos.ModerationJob;
import com.vsnt.dtos.ResolutionEnum;
import com.vsnt.dtos.TranscodingSegmentUpdateDTO;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;
public class HlsDirectoryWatcher implements Runnable {

    private final WatchService watchService;
    private final Map<WatchKey, Path> keyDirectoryMap = new HashMap<>();
    private final SegmentEventFactory segmentEventFactory;
    private final SegmentEventProducer producer;
    private final String assetId ;
    private final String mediaId;

    private final ExecutorService executor;

    private final CompletableFuture<Void> completionFuture = new CompletableFuture<>();

    private volatile boolean running = false;
    private Thread watcherThread;


    public HlsDirectoryWatcher(String basePath,
                               SegmentEventFactory segmentEventFactory,
                               SegmentEventProducer producer, String assetId, String mediaId) throws IOException {

        this.segmentEventFactory = segmentEventFactory;
        this.producer = producer;
        this.assetId = assetId;
        this.mediaId = mediaId;


        this.watchService = FileSystems.getDefault().newWatchService();

        //  Thread pool with backpressure
        this.executor = new ThreadPoolExecutor(
                4, 8,
                60, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(1000),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );

        // IMPORTANT: use numeric folders (matches FFmpeg %v)
        String[] variants = {"0", "1", "2", "3"};

        for (String variant : variants) {
            Path dir = Paths.get(basePath,variant);
            registerDirectory(dir);
        }
    }

    public CompletableFuture<Void> getCompletionFuture() {
        return completionFuture;
    }

    private void registerDirectory(Path dir) throws IOException {
        WatchKey key = dir.register(watchService, ENTRY_CREATE);
        keyDirectoryMap.put(key, dir);
        System.out.println("Watching directory: " + dir);
    }

    public synchronized void start() {
        if (running) return;

        running = true;
        watcherThread = new Thread(this);
        watcherThread.setName("hls-directory-watcher");
        watcherThread.start();
    }

    public synchronized void stop() {
        running = false;

        try {
            watchService.close();
        } catch (IOException ignored) {}

        executor.shutdown();
    }

    private void processSegment(Path fullPath) {

        executor.submit(() -> {
            try {
                //  Ignore temp files
                if (fullPath.toString().endsWith(".tmp")) return;

                //  Only process final .ts
                if (!fullPath.toString().endsWith(".ts")) return;

                System.out.println("Processing: " + fullPath);

                TranscodingSegmentUpdateDTO update =
                        segmentEventFactory.generate(fullPath,assetId,mediaId, MediaType.STATIC);

                producer.sendEvent(update);

                Files.deleteIfExists(fullPath);

            } catch (Exception e) {
                System.err.println("Failed: " + fullPath + " — " + e.getMessage());
            }
        });
    }

    private void drainRemainingFiles() {

        System.out.println("Draining remaining segments...");

        for (Path dir : keyDirectoryMap.values()) {
            try (DirectoryStream<Path> stream =
                         Files.newDirectoryStream(dir, "*.ts")) {

                for (Path file : stream) {
                    processSegment(file);
                }

            } catch (IOException e) {
                System.err.println("Drain failed: " + dir);
            }
        }
    }

    @Override
    public void run() {
        System.out.println("Watcher started: " + Thread.currentThread().getName());
        try {
            while (running) {
                WatchKey key;
                try {
                    key = watchService.poll(2, TimeUnit.SECONDS);
                    if (key == null) continue;

                } catch (InterruptedException | ClosedWatchServiceException e) {
                    break;
                }
                Path dir = keyDirectoryMap.get(key);
                if (dir == null) continue;
                for (WatchEvent<?> event : key.pollEvents()) {
                    if (event.kind() == OVERFLOW) continue;
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
            } catch (InterruptedException ignored) {}

            completionFuture.complete(null);

            System.out.println("Watcher stopped cleanly.");
        }
    }
}