package com.vsnt;

import com.vsnt.config.FFMPEGConfigVODEncrypted;
import com.vsnt.dtos.MediaType;
import com.vsnt.services.HlsKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.*;
import java.util.List;
import java.util.concurrent.*;

public class VideoTranscoder {

    private static final Logger logger = LoggerFactory.getLogger(VideoTranscoder.class);

    private static final long FFMPEG_TIMEOUT_MINUTES = 30L;

    private final ExecutorService executor;

    public VideoTranscoder(ExecutorService executor) {
        this.executor = executor;
    }
    public CompletableFuture<Boolean> transcode(
            String url,
            String outputPath,
            String hexKey,
            MediaType mediaType,
            String publicKeyURL
    ) {
        logger.info("Submitting transcoding job. url={}, outputPath={}", url, outputPath);

        return CompletableFuture
                .supplyAsync(() -> {
                    try {
                        return setupEncryptionKeys(hexKey, outputPath, publicKeyURL);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }, executor)
                .thenApplyAsync(keyInfoPath -> buildCommands(url, outputPath, keyInfoPath), executor)
                .thenComposeAsync(this::runAllAsync, executor)
                .whenComplete((success, ex) -> {
                    if (ex != null) logger.error("Transcoding pipeline failed. url={}", url, ex);
                    else if (success)  logger.info("Transcoding completed. outputPath={}", outputPath);
                    else               logger.warn("One or more FFmpeg tasks failed. outputPath={}", outputPath);
                });
    }

    private Path setupEncryptionKeys(String hexKey, String outputPath, String publicKeyURL) throws IOException {
        Path basePath    = Paths.get(outputPath);
        Path keyFilePath = HlsKeyUtil.createKeyFile(hexKey, outputPath);
        Path keyInfoPath = basePath.resolve("key_info.txt");

        Files.writeString(keyInfoPath, publicKeyURL + "\n" + keyFilePath.toAbsolutePath());

        logger.debug("Encryption keys written. keyInfo={}", keyInfoPath);
        return keyInfoPath;
    }

    private List<String> buildCommands(String url, String outputPath, Path keyInfoPath) {
        List<String> commands = new FFMPEGConfigVODEncrypted(
                url,
                outputPath,
                keyInfoPath.toAbsolutePath().toString()
        ).getFFMPEGCommands();

        logger.info("Generated {} FFmpeg commands", commands.size());
        return commands;
    }


    private CompletableFuture<Boolean> runAllAsync(List<String> commands) {
        List<CompletableFuture<Boolean>> futures = commands.stream()
                .map(cmd -> CompletableFuture
                        .supplyAsync(() -> executeFFmpeg(cmd), executor)
                        .exceptionally(ex -> {
                            logger.error("FFmpeg task threw exception. cmd={}", cmd, ex);
                            return false;
                        }))
                .toList();
        return CompletableFuture
                .allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .anyMatch(sf->sf.getNow(false))
                );
    }

    private boolean executeFFmpeg(String command) {
        try {
            logger.info("Executing FFmpeg command: {}", command);

            Process process = new ProcessBuilder("sh", "-c", command)
                    .redirectErrorStream(true)
                    .start();

            try (BufferedReader reader =
                         new BufferedReader(
                                 new InputStreamReader(process.getInputStream()))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    logger.debug("[FFMPEG] {}", line);
                }
            }
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                logger.info("FFmpeg command succeeded");
                return true;
            }
            logger.warn("FFmpeg command failed. exitCode={}", exitCode);
            return false;

        } catch (Exception e) {
            logger.error("Error executing FFmpeg command", e);
            return false;
        }
    }
    public void shutdown() {
        logger.info("Shutting down transcoder executor");
        executor.shutdown();
    }
}