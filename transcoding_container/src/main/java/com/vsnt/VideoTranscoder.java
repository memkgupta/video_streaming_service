package com.vsnt;

import com.vsnt.config.FFMPEGConfig;
import com.vsnt.services.HlsKeyUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.*;
import java.util.List;
import java.util.concurrent.*;

public class VideoTranscoder {

    private final ExecutorService executor =
            Executors.newFixedThreadPool(4); // 4 resolutions

    public boolean startTranscodingAsync(
            String url,
            String outputPath,
            String hexKey,
            String publicKeyURL) throws IOException {
//        String[] resolutions = {"360p", "480p", "720p", "1080p"};
        Path basePath = Paths.get(outputPath);

//        for (String resolution : resolutions) {
//            Files.createDirectories(basePath.resolve(resolution));
//        }

        // Generate key file
        Path keyFilePath =
                HlsKeyUtil.createKeyFile(hexKey, outputPath );

        Path keyInfoPath = basePath.resolve("key_info.txt");

        String keyInfoContent =
                publicKeyURL + "\n" +
                        keyFilePath.toAbsolutePath() + "\n" +
                        hexKey;

        Files.writeString(keyInfoPath, keyInfoContent);

        try {



            FFMPEGConfig config =
                    new FFMPEGConfig(url,
                            outputPath,
                            keyInfoPath.toAbsolutePath().toString());

            List<String> commands = config.getFfmpegCommands();

            // Run resolutions in parallel
            List<Future<Boolean>> results =
                    commands.stream()
                            .map(cmd -> executor.submit(() -> executeFFmpeg(cmd)))
                            .toList();

            // Wait for all to complete
            for (Future<Boolean> future : results) {
                if (!future.get()) {
                    return false;
                }
            }

            System.out.println("Transcoding completed successfully.");
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean executeFFmpeg(String command) {

        try {

            System.out.println("Executing: " + command);

            ProcessBuilder pb = new ProcessBuilder("sh", "-c", command);
            pb.redirectErrorStream(true);

            Process process = pb.start();



            int exitCode = process.waitFor();
            return exitCode == 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void shutdown() {
        executor.shutdown();
    }
}