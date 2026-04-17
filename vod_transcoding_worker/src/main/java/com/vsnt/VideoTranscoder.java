package com.vsnt;


import com.vsnt.config.FFMPEGConfigVOD;
import com.vsnt.config.FFMPEGConfigVODEncrypted;
import com.vsnt.dtos.MediaType;
import com.vsnt.services.HlsKeyUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class VideoTranscoder {
  private final ExecutorService executor;
    public VideoTranscoder(ExecutorService executor) {
        this.executor = executor;
    }
    public boolean startTranscodingAsync(
            String url,
            String outputPath,
            String hexKey,
            MediaType mediaType,
            String publicKeyURL)throws IOException {

        Path basePath = Paths.get(outputPath);
        // Generate key file
        Path keyFilePath =
                HlsKeyUtil.createKeyFile(hexKey, outputPath );
        Path keyInfoPath = basePath.resolve("key_info.txt");
        String keyInfoContent =
                publicKeyURL + "\n" +
                        keyFilePath.toAbsolutePath();
        Files.writeString(keyInfoPath, keyInfoContent);
        try {
//            FFMPEGConfigVOD config = new FFMPEGConfigVOD (
//                    url,
//                    outputPath,
//                    keyInfoPath.toAbsolutePath().toString()
//            );
            FFMPEGConfigVODEncrypted config = new FFMPEGConfigVODEncrypted(
                    url,
                    outputPath,
                    keyInfoPath.toAbsolutePath().toString()
            );
            List<String> commands = config.getFFMPEGCommands();
            // Run resolutions in parallel
            List<Future<Boolean>> results =
                    commands.stream()
                            .map(cmd -> executor.submit(() -> executeffmpeg(cmd)))
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

    private boolean executeffmpeg(String command) {

        try {

            System.out.println("Executing: " + command);

            ProcessBuilder pb = new ProcessBuilder("sh", "-c", command);
            pb.redirectErrorStream(true);
            pb.inheritIO();
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