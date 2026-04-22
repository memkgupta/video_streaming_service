package com.vsnt;

import com.vsnt.config.FFMPEGConfigLive;
import com.vsnt.dtos.MediaType;
import com.vsnt.dtos.TranscodeResult;
import com.vsnt.services.HlsKeyUtil;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

public class VideoTranscoder {

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

            List<Process> processes = new CopyOnWriteArrayList<>();
            activeStreams.put(streamId, processes);

            List<Future<Boolean>> results =
                    commands.stream()
                            .map(cmd -> executor.submit(() -> executeFFmpeg(cmd, processes)))
                            .toList();

            int successCount = 0;

            for (Future<Boolean> future : results) {
                try {
                    if (future.get()) {
                        successCount++;
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    stopStream(streamId);
                    return new TranscodeResult(
                            TranscodeResult.Status.INTERRUPTED,
                            streamId,
                            "Thread interrupted"
                    );
                } catch (ExecutionException e) {
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
                return new TranscodeResult(
                        TranscodeResult.Status.STOPPED,
                        streamId,
                        "Stopped by system (revoke/shutdown)"
                );
            }

            if (successCount == results.size()) {
                return new TranscodeResult(
                        TranscodeResult.Status.SUCCESS,
                        streamId,
                        "All renditions completed"
                );
            }

            return new TranscodeResult(
                    TranscodeResult.Status.PARTIAL_FAILURE,
                    streamId,
                    "Some renditions failed"
            );

        } catch (Exception e) {
            stopStream(streamId);

            return new TranscodeResult(
                    TranscodeResult.Status.FAILED,
                    streamId,
                    "Exception: " + e.getMessage()
            );
        }
    }

    private boolean executeFFmpeg(String command, List<Process> processes) {
        try {
            System.out.println("Executing: " + command);

            ProcessBuilder pb = new ProcessBuilder("sh", "-c", command);
            pb.redirectErrorStream(true);
            pb.inheritIO();

            Process process = pb.start();
            pb.inheritIO();
            processes.add(process);

            int exitCode = process.waitFor();

            processes.remove(process);

            return exitCode == 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void stopAllStreams() {
        System.out.println("Stopping ALL streams");
        isRevoked = true;
        for (String streamId : activeStreams.keySet()) {
            stopStream(streamId);
        }
    }
    public void stopStream(String streamId) {
        List<Process> processes = activeStreams.remove(streamId);

        if (processes != null) {
            System.out.println("Stopping stream: " + streamId);

            for (Process process : processes) {
                try {
                    process.destroy();
                    // force kill if needed
                    if (process.isAlive()) {
                        process.destroyForcibly();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Set<String> getActiveStreams() {
        return activeStreams.keySet();
    }

  public void restart()
  {
      isRevoked = false;
  }
}