package com.vsnt;

import com.vsnt.config.FFMPEGConfigVODEncrypted;
import com.vsnt.dtos.MediaType;
import com.vsnt.services.HlsKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class VideoTranscoder {

    private static final Logger logger = LoggerFactory.getLogger(VideoTranscoder.class);

    private final ExecutorService executor;

    public VideoTranscoder(ExecutorService executor) {
        this.executor = executor;
    }

    public boolean startTranscodingAsync(
            String url,
            String outputPath,
            String hexKey,
            MediaType mediaType,
            String publicKeyURL) throws IOException {

        logger.info("Starting VOD transcoding. url={}, outputPath={}", url, outputPath);

        Path basePath = Paths.get(outputPath);

        // key setup
        Path keyFilePath = HlsKeyUtil.createKeyFile(hexKey, outputPath);
        Path keyInfoPath = basePath.resolve("key_info.txt");

        String keyInfoContent =
                publicKeyURL + "\n" + keyFilePath.toAbsolutePath();

        Files.writeString(keyInfoPath, keyInfoContent);

        try {
            FFMPEGConfigVODEncrypted config = new FFMPEGConfigVODEncrypted(
                    url,
                    outputPath,
                    keyInfoPath.toAbsolutePath().toString()
            );

            List<String> commands = config.getFFMPEGCommands();
            logger.info("Generated {} FFmpeg commands", commands.size());

            List<Future<Boolean>> results =
                    commands.stream()
                            .map(cmd -> executor.submit(() -> executeFFmpeg(cmd)))
                            .toList();

            for (Future<Boolean> future : results) {
                if (!future.get()) {
                    logger.warn("One of the FFmpeg tasks failed");
                    return false;
                }
            }

            logger.info("Transcoding completed successfully");
            return true;

        } catch (Exception e) {
            logger.error("Transcoding failed", e);
            return false;
        }
    }

    private boolean executeFFmpeg(String command) {
        try {
            logger.debug("Executing FFmpeg command: {}", command);

            ProcessBuilder pb = new ProcessBuilder("sh", "-c", command);
            pb.redirectErrorStream(true);
            pb.inheritIO();

            Process process = pb.start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                logger.debug("FFmpeg command completed successfully");
                return true;
            } else {
                logger.warn("FFmpeg command failed with exitCode={}", exitCode);
                return false;
            }

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