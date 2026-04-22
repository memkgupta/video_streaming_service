package com.vsnt;

import com.vsnt.config.FFMPEGConfigLive;
import com.vsnt.dtos.MediaType;
import com.vsnt.dtos.TranscodeResult;
import com.vsnt.services.HlsKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

public class VideoTranscoder {

    private static final Logger logger = LoggerFactory.getLogger(VideoTranscoder.class);

    private final ExecutorService executor;

    private volatile boolean isRevoked = false;
    private final ConcurrentHashMap<String, List<Process>> activeStreams = new ConcurrentHashMap<>();

    public VideoTranscoder(ExecutorService executor) {
        this.executor = executor;
    }

    public TranscodeResult startTranscodingAsync(
            String streamId,
            String url,
            String outputPath,
            String hexKey,
            MediaType mediaType,
            String publicKeyURL) throws IOException {

        logger.info("Starting transcoding. streamId={}, url={}", streamId, url);

        Path basePath = Paths.get(outputPath);
        Path keyFilePath = HlsKeyUtil.createKeyFile(hexKey, outputPath);
        Path keyInfoPath = basePath.resolve("key_info.txt");

        String keyInfoContent =
                publicKeyURL + "\n" +
                        keyFilePath.toAbsolutePath();

        Files.writeString(keyInfoPath, keyInfoContent);

        try {
            FFMPEGConfigLive config = new FFMPEGConfigLive(
                    url,
                    outputPath,
                    keyInfoPath.toAbsolutePath().toString()
            );

            List<String> commands = config.getFFMPEGCommands();

            logger.info("Generated {} FFmpeg commands for streamId={}", commands.size(), streamId);

            List<Process> processes = new CopyOnWriteArrayList<>();
            activeStreams.put(streamId, processes);

            List<Future<Boolean>> results =
                    commands.stream()
                            .map(cmd -> executor.submit(() -> executeFFmpeg(streamId, cmd, processes)))
                            .toList();

            int successCount = 0;

            for (Future<Boolean> future : results) {
                try {
                    if (future.get()) {
                        successCount++;
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.warn("Transcoding interrupted. streamId={}", streamId);
                    stopStream(streamId);
                    return new TranscodeResult(
                            TranscodeResult.Status.INTERRUPTED,
                            streamId,
                            "Thread interrupted"
                    );
                } catch (ExecutionException e) {
                    logger.error("Execution error in FFmpeg. streamId={}", streamId, e);
                    stopStream(streamId);
                    return new TranscodeResult(
                            TranscodeResult.Status.FAILED,
                            streamId,
                            "Execution error: " + e.getMessage()
                    );
                }
            }

            activeStreams.remove(streamId);

            if (isRevoked) {
                logger.warn("Transcoding stopped due to revoke. streamId={}", streamId);
                return new TranscodeResult(
                        TranscodeResult.Status.STOPPED,
                        streamId,
                        "Stopped by system"
                );
            }

            if (successCount == results.size()) {
                logger.info("Transcoding SUCCESS. streamId={}", streamId);
                return new TranscodeResult(
                        TranscodeResult.Status.SUCCESS,
                        streamId,
                        "All renditions completed"
                );
            }

            logger.warn("Partial failure in transcoding. streamId={}, successCount={}/{}",
                    streamId, successCount, results.size());

            return new TranscodeResult(
                    TranscodeResult.Status.PARTIAL_FAILURE,
                    streamId,
                    "Some renditions failed"
            );

        } catch (Exception e) {
            logger.error("Fatal error in transcoding. streamId={}", streamId, e);
            stopStream(streamId);

            return new TranscodeResult(
                    TranscodeResult.Status.FAILED,
                    streamId,
                    "Exception: " + e.getMessage()
            );
        }
    }

    private boolean executeFFmpeg(String streamId, String command, List<Process> processes) {
        try {
            logger.info("Executing FFmpeg. streamId={}, cmd={}", streamId, command);

            ProcessBuilder pb = new ProcessBuilder("sh", "-c", command);
            pb.redirectErrorStream(true);
            pb.inheritIO();

            Process process = pb.start();
            processes.add(process);

            int exitCode = process.waitFor();

            processes.remove(process);

            if (exitCode == 0) {
                logger.debug("FFmpeg process completed successfully. streamId={}", streamId);
                return true;
            } else {
                logger.warn("FFmpeg process failed. streamId={}, exitCode={}", streamId, exitCode);
                return false;
            }

        } catch (Exception e) {
            logger.error("Error executing FFmpeg. streamId={}", streamId, e);
            return false;
        }
    }

    public void stopAllStreams() {
        logger.warn("Stopping ALL streams (revoke triggered)");
        isRevoked = true;

        for (String streamId : activeStreams.keySet()) {
            stopStream(streamId);
        }
    }

    public void stopStream(String streamId) {
        List<Process> processes = activeStreams.remove(streamId);

        if (processes != null) {
            logger.warn("Stopping stream. streamId={}", streamId);

            for (Process process : processes) {
                try {
                    process.destroy();

                    if (process.isAlive()) {
                        process.destroyForcibly();
                    }

                } catch (Exception e) {
                    logger.error("Error stopping process. streamId={}", streamId, e);
                }
            }
        }
    }

    public Set<String> getActiveStreams() {
        return activeStreams.keySet();
    }

    public void restart() {
        logger.info("Resetting revoke state. System back to RUNNING");
        isRevoked = false;
    }
}