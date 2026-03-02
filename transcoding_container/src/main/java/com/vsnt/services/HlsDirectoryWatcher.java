package com.vsnt.services;

import com.vsnt.SegmentEventFactory;
import com.vsnt.SegmentEventProducer;
import com.vsnt.dtos.TranscodingSegmentUpdateDTO;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

import static java.nio.file.StandardWatchEventKinds.*;

public class HlsDirectoryWatcher implements Runnable {

    private final WatchService watchService;
    private final Map<WatchKey, Path> keyDirectoryMap = new HashMap<>();
    private final SegmentEventFactory segmentEventFactory;
    private final SegmentEventProducer producer;
    private final CompletableFuture<Void> completionFuture = new CompletableFuture<>();

    private volatile boolean running = false;
    private Thread watcherThread;

    public HlsDirectoryWatcher(String basePath,
                               SegmentEventFactory segmentEventFactory,
                               SegmentEventProducer producer) throws IOException {

        this.segmentEventFactory = segmentEventFactory;
        this.producer = producer;
        this.watchService = FileSystems.getDefault().newWatchService();

        String[] resolutions = {"360p", "480p", "720p", "1080p"};

        for (String resolution : resolutions) {
            Path dir = Paths.get(basePath, resolution);
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


    }

    private void processSegment(Path fullPath) {

        System.out.println("Segment detected: " + fullPath);

        try {
            TranscodingSegmentUpdateDTO update =
                    segmentEventFactory.generate(fullPath);

            producer.sendEvent(update);

            Files.deleteIfExists(fullPath);

            System.out.println("Processed & deleted: " + fullPath);

        } catch (Exception e) {
            System.err.println("Failed to process segment: " + fullPath + " — " + e.getMessage());
        }
    }

    private void drainRemainingFiles() {

        System.out.println("Draining remaining segments before shutdown...");

        for (Path dir : keyDirectoryMap.values()) {
            try (DirectoryStream<Path> stream =
                         Files.newDirectoryStream(dir, "*.ts")) {

                for (Path file : stream) {
                    processSegment(file);
                }

            } catch (IOException e) {
                System.err.println("Failed to drain directory: " + dir);
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

                    Path fullPath =
                            dir.resolve((Path) event.context());

                    if (fullPath.toString().endsWith(".ts")) {
                        processSegment(fullPath);
                    }
                }

                key.reset();
            }

        } finally {

            // 🔥 Important: final drain before exit
            drainRemainingFiles();

            completionFuture.complete(null);

            System.out.println("Watcher stopped cleanly.");
        }
    }
}