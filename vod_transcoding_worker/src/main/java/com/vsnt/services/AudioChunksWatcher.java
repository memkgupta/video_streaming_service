package com.vsnt.services;

import com.vsnt.AudioChunkEventFactory;
import com.vsnt.TranscriptionJobProducer;
import com.vsnt.dtos.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static java.nio.file.StandardWatchEventKinds.*;

public class AudioChunksWatcher implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(AudioChunksWatcher.class);


    private static final String AUDIO_CHUNK_EXTENSION  = ".mp3";
    private static final String SEGMENT_DONE_SENTINEL  = "seg_list.txt";
    private static final int    MAX_RETRIES            = 3;
    private static final long   RETRY_BACKOFF_MS       = 500L;
    private static final long   FILE_WRITE_POLL_MS     = 200L;
    private static final long   WATCH_POLL_TIMEOUT_SEC = 2L;
    private static final long   EXECUTOR_SHUTDOWN_SEC  = 30L;


    private final AudioChunkEventFactory  audioChunkEventFactory;
    private final TranscriptionJobProducer producer;
    private final WatchService            watchService;
    private final ExecutorService         executor;

    private final Path    outputDir;
    private final String  assetId;
    private final String  mediaId;
    private final int     chunkDurationSeconds;
    private final CompletableFuture<Void> completionFuture = new CompletableFuture<>();
    private volatile boolean running = false;


    public AudioChunksWatcher(
            String basePath,
            AudioChunkEventFactory audioChunkEventFactory,
            TranscriptionJobProducer producer,
            String assetId,
            String mediaId,
            int chunkDurationSeconds
    ) throws IOException {
        this.audioChunkEventFactory  = audioChunkEventFactory;
        this.producer                = producer;
        this.assetId                 = assetId;
        this.mediaId                 = mediaId;
        this.chunkDurationSeconds    = chunkDurationSeconds;
        this.outputDir               = Paths.get(basePath, "audio");
        this.watchService            = FileSystems.getDefault().newWatchService();
        this.executor                = buildExecutor();

        outputDir.register(watchService, ENTRY_CREATE);

        logger.info("AudioChunksWatcher initialized. mediaId={}, assetId={}, chunkDuration={}s",
                mediaId, assetId, chunkDurationSeconds);
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
        Thread t = new Thread(this, "audio-chunks-watcher-" + mediaId);
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
            if (key == null) continue;               // null = timeout or done signal handled inside pollKey

            for (WatchEvent<?> event : key.pollEvents()) {
                handleEvent(event);
            }

            key.reset();
        }
    }

    /** Returns null on timeout-and-done, breaks loop via running=false */
    private WatchKey pollKey() {
        try {
            WatchKey key = watchService.poll(WATCH_POLL_TIMEOUT_SEC, TimeUnit.SECONDS);
            if (key == null && isFfmpegDone()) {
                logger.info("FFmpeg done signal detected. mediaId={}", mediaId);
                running = false;
            }
            return key;
        } catch (InterruptedException | ClosedWatchServiceException e) {
            logger.warn("Watcher interrupted or closed. mediaId={}", mediaId);
            running = false;
            return null;
        }
    }

    private void handleEvent(WatchEvent<?> event) {
        if (event.kind() == OVERFLOW) {
            logger.warn("WatchService overflow — some events may have been lost. mediaId={}", mediaId);
            return;
        }
        Path fullPath = outputDir.resolve((Path) event.context());
        String filename = fullPath.getFileName().toString();

        if (!filename.endsWith(AUDIO_CHUNK_EXTENSION)) return;
        try {
            waitUntilWritten(fullPath);
            Path renamed = renameChunk(fullPath);
            processSegment(renamed);
        } catch (IOException | InterruptedException e) {
            logger.error("Failed to handle chunk. file={}, mediaId={}", fullPath, mediaId, e);
        }
    }



    private void processSegment(Path fullPath) {
        executor.submit(() -> processWithRetry(fullPath));
    }

    private void processWithRetry(Path fullPath) {
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                producer.publish(audioChunkEventFactory.generate(
                        fullPath, assetId, mediaId, MediaType.STATIC
                ));
                Files.deleteIfExists(fullPath);
                logger.debug("Chunk processed and deleted. file={}, mediaId={}", fullPath, mediaId);
                return;

            } catch (Exception e) {
                logger.warn("Chunk processing failed. attempt={}/{}, file={}, mediaId={}",
                        attempt, MAX_RETRIES, fullPath, mediaId, e);

                if (attempt < MAX_RETRIES) sleepBackoff(attempt);
            }
        }
        logger.error("Chunk permanently failed after {} retries. file={}, mediaId={}",
                MAX_RETRIES, fullPath, mediaId);
    }



    private void drainRemainingFiles() {
        logger.info("Draining remaining chunks. mediaId={}", mediaId);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(
                outputDir, "*" + AUDIO_CHUNK_EXTENSION)) {       // ✅ was *.ts, now *.mp3
            for (Path file : stream) {
                processSegment(file);
            }
        } catch (IOException e) {
            logger.error("Drain failed. dir={}, mediaId={}", outputDir, mediaId, e);
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

    private void closeWatchService() {
        try {
            watchService.close();
        } catch (IOException e) {
            logger.error("Error closing WatchService. mediaId={}", mediaId, e);
        }
    }



    /** Blocks until file size stabilizes (FFmpeg finished writing) */
    private void waitUntilWritten(Path file) throws InterruptedException {
        long previousSize = -1;
        while (true) {
            long currentSize = file.toFile().length();
            if (currentSize > 0 && currentSize == previousSize) return;
            previousSize = currentSize;
            Thread.sleep(FILE_WRITE_POLL_MS);
        }
    }

    /** chunk_003.mp3 → 45_60_003.mp3 based on injected chunkDurationSeconds */
    private Path renameChunk(Path chunk) throws IOException {
        String name  = chunk.getFileName().toString();
        int index    = Integer.parseInt(name.replaceAll("[^0-9]", ""));
        int start    = index * chunkDurationSeconds;
        int end      = (index + 1) * chunkDurationSeconds;
        String newName = String.format("%d_%d_%03d.mp3", start, end, index);
        Path renamed = chunk.resolveSibling(newName);
        Files.move(chunk, renamed, StandardCopyOption.REPLACE_EXISTING);
        logger.debug("Renamed chunk. {} → {}", chunk.getFileName(), newName);
        return renamed;
    }

    private boolean isFfmpegDone() {
        return outputDir.resolve(SEGMENT_DONE_SENTINEL).toFile().exists();
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