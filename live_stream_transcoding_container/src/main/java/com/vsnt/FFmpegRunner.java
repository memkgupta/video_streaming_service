package com.vsnt;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FFmpegRunner {

    public Process start() throws IOException {

        System.out.println("[INFO] Starting FFmpeg runner...");

        String rtmpUrl = "rtmp://host.docker.internal:1935/live/" + AppConfig.MEDIA_ID;

        System.out.println("[INFO] Reading from RTMP: " + rtmpUrl);

        // Ensure output directory exists
        Files.createDirectories(Paths.get(AppConfig.OUTPUT_DIR));

        ProcessBuilder builder = new ProcessBuilder(
                "ffmpeg",

                // overwrite output
                "-y",

                // input RTMP
                "-i", rtmpUrl,

                // video codec
                "-c:v", "libx264",
                "-preset", "veryfast",

                // audio codec (CRITICAL)
                "-c:a", "aac",
                "-b:a", "128k",

                // keyframe settings (CRITICAL)
                "-g", "48",
                "-keyint_min", "48",
                "-sc_threshold", "0",

                // HLS settings
                "-f", "hls",
                "-hls_time", "4",
                "-hls_list_size", "6",

                // VERY IMPORTANT FLAGS


                // ensure MPEGTS format
                "-hls_segment_type", "mpegts",

                // segment filename
                "-hls_segment_filename",
                AppConfig.OUTPUT_DIR + "/segment_%07d.ts",

                // output playlist
                AppConfig.OUTPUT_DIR + "/index.m3u8"
        );


        Process process = builder.start();

        System.out.println("[INFO] FFmpeg process started, PID=" + process.pid());

        // Thread 1: Read FFmpeg stderr (IMPORTANT: FFmpeg logs here)
//        new Thread(() -> {
//            try (BufferedReader reader = new BufferedReader(
//                    new InputStreamReader(process.getErrorStream()))) {
//
//                String line;
//
//                while ((line = reader.readLine()) != null) {
//                    System.out.println("[FFmpeg-ERR] " + line);
//                }
//
//                System.out.println("[FFmpeg-ERR] Stream closed");
//
//            } catch (Exception e) {
//                System.out.println("[ERROR] Failed reading FFmpeg stderr");
//                e.printStackTrace();
//            }
//        }, "ffmpeg-stderr-thread").start();

        // Thread 2: Read FFmpeg stdout
        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {

                String line;

                while ((line = reader.readLine()) != null) {
                    System.out.println("[FFmpeg-OUT] " + line);
                }

                System.out.println("[FFmpeg-OUT] Stream closed");

            } catch (Exception e) {
                System.out.println("[ERROR] Failed reading FFmpeg stdout");
                e.printStackTrace();
            }
        }, "ffmpeg-stdout-thread").start();

        // Thread 3: Monitor FFmpeg exit
        new Thread(() -> {
            try {

                int exitCode = process.waitFor();

                System.out.println("[INFO] FFmpeg exited with code: " + exitCode);

            } catch (InterruptedException e) {

                System.out.println("[ERROR] FFmpeg wait interrupted");

                e.printStackTrace();

            }
        }, "ffmpeg-monitor-thread").start();

        return process;
    }
}