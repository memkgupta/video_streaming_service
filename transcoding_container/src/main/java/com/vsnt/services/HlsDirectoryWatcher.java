package com.vsnt.services;

import com.vsnt.SegmentEventFactory;
import com.vsnt.SegmentEventProducer;
import com.vsnt.dtos.TranscodingSegmentUpdateDTO;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

import static java.nio.file.StandardWatchEventKinds.*;

public class HlsDirectoryWatcher implements Runnable {

    private final WatchService watchService;
    private final Map<WatchKey, Path> keyDirectoryMap = new HashMap<>();
    private final SegmentEventFactory segmentEventFactory;
    private final SegmentEventProducer producer;
    private volatile boolean running = false;
    private Thread watcherThread;

    public HlsDirectoryWatcher(String basePath,
                               SegmentEventFactory segmentEventFactory, SegmentEventProducer producer)
            throws IOException {

        this.segmentEventFactory = segmentEventFactory;
        this.producer = producer;
        this.watchService = FileSystems.getDefault().newWatchService();

        String[] resolutions = {"360p", "480p", "720p", "1080p"};

        for (String resolution : resolutions) {
            Path dir = Paths.get(basePath, resolution);
            registerDirectory(dir);
        }
    }

    private void registerDirectory(Path dir) throws IOException {

        WatchKey key = dir.register(
                watchService,
                ENTRY_CREATE,
                ENTRY_MODIFY
        );

        keyDirectoryMap.put(key, dir);
        System.out.println("Watching directory: " + dir);
    }

    // ✅ Start in background thread
    public void start() {
        if (running) return;

        running = true;
        watcherThread = new Thread(this);
        watcherThread.setName("hls-directory-watcher");
        watcherThread.start();
    }

    // ✅ Graceful shutdown
    public void stop() {
        running = false;
        try {
            watchService.close();
        } catch (IOException ignored) {}
    }

    @Override
    public void run() {

        System.out.println("File watcher started in thread: "
                + Thread.currentThread().getName());

        while (running) {

            WatchKey key;

            try {
                key = watchService.take();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (ClosedWatchServiceException e) {
                break;
            }

            Path dir = keyDirectoryMap.get(key);
            if (dir == null) continue;

            for (WatchEvent<?> event : key.pollEvents()) {

                WatchEvent.Kind<?> kind = event.kind();

                if (kind == OVERFLOW) continue;

                WatchEvent<Path> ev = (WatchEvent<Path>) event;
                Path fullPath = dir.resolve(ev.context());

                // Only process .ts files
                if (fullPath.toString().endsWith(".ts")) {

                    System.out.println("Segment detected: " + fullPath);

                    TranscodingSegmentUpdateDTO update =
                            segmentEventFactory.generate(fullPath);
                    producer.sendEvent(update);
                    // 🔥 Send to Kafka here
                }
            }

            boolean valid = key.reset();
            if (!valid) {
                keyDirectoryMap.remove(key);
                if (keyDirectoryMap.isEmpty()) break;
            }
        }

        System.out.println("Watcher stopped.");
    }
}