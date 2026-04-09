package com.vsnt.services;


import com.vsnt.ModerationJobProducer;
import com.vsnt.SegmentEventFactory;
import com.vsnt.SegmentEventProducer;
import com.vsnt.dtos.ModerationJob;
import com.vsnt.dtos.TranscodingSegmentUpdateDTO;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

import static java.nio.file.StandardWatchEventKinds.*;

public class Mp4SegmentDirectoryWatcher implements Runnable {

    private final WatchService watchService;
    private final Map<WatchKey, Path> keyDirectoryMap = new HashMap<>();
    private final SegmentEventFactory segmentEventFactory;
    private final ModerationJobProducer producer;

    private final ExecutorService executor;
    private final CompletableFuture<Void> completionFuture = new CompletableFuture<>();

    private volatile boolean running = false;
    private Thread watcherThread;

    public Mp4SegmentDirectoryWatcher(String basePath,
                                      SegmentEventFactory segmentEventFactory,
                                      ModerationJobProducer producer) throws IOException {

        this.segmentEventFactory = segmentEventFactory;
        this.producer = producer;
        this.watchService = FileSystems.getDefault().newWatchService();

        this.executor = new ThreadPoolExecutor(
                4, 8,
                60, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(1000),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );

        //  For MP4 segmentation → usually single directory
        Path dir = Paths.get(basePath);
        registerDirectory(dir);
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
        watcherThread.setName("mp4-segment-watcher");
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
                String file = fullPath.toString();

                //  ignore temp / incomplete files

                if (!file.endsWith(".mp4")) return;

                // wait until file is fully written
                waitUntilStable(fullPath);

                System.out.println("Processing MP4: " + fullPath);

                ModerationJob update =
                        segmentEventFactory.generateModerationJob(fullPath);

                producer.send(update);

                //  delete after processing
                Files.deleteIfExists(fullPath);

            } catch (Exception e) {
                System.err.println("Failed: " + fullPath + " — " + e.getMessage());
            }
        });
    }

    //  ensures FFmpeg finished writing
    private void waitUntilStable(Path path) throws IOException, InterruptedException {

        long prevSize = -1;

        while (true) {
            long size = Files.size(path);

            if (size == prevSize) {
                return; // stable → done writing
            }

            prevSize = size;
            Thread.sleep(2000); // tune if needed
        }
    }

    private void drainRemainingFiles() {

        System.out.println("Draining remaining MP4 segments...");

        for (Path dir : keyDirectoryMap.values()) {
            try (DirectoryStream<Path> stream =
                         Files.newDirectoryStream(dir, "*.mp4")) {

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

        System.out.println("MP4 Watcher started: " + Thread.currentThread().getName());

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

            System.out.println("MP4 Watcher stopped cleanly.");
        }
    }
}
