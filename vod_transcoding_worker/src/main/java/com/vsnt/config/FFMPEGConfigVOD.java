package com.vsnt.config;

import java.util.List;

public class FFMPEGConfigVOD  {


    private String filePath;
    private String outputPath;
    private String encryptionKey;

    public FFMPEGConfigVOD(String filePath, String outputPath, String encryptionKey) {

    this.filePath = filePath;
    this.outputPath = outputPath;
    this.encryptionKey = encryptionKey;

    }


    public List<String> getFFMPEGCommands() {

        String command = String.format(
                "ffmpeg -i \"%s\" " +

                        "-filter_complex \"" +
                        "[0:v]split=4[v1][v2][v3][v4];" +
                        "[v1]scale=640:360[v360];" +
                        "[v2]scale=854:480[v480];" +
                        "[v3]scale=1280:720[v720];" +
                        "[v4]scale=1920:1080[v1080]" +
                        "\" " +

                        // mapping
                        "-map \"[v360]\" -map 0:a " +
                        "-map \"[v480]\" -map 0:a " +
                        "-map \"[v720]\" -map 0:a " +
                        "-map \"[v1080]\" -map 0:a " +

                        // codecs
                        "-c:v libx264 -preset veryfast " +
                        "-c:a aac " +

                        // bitrate per stream
                        "-b:v:0 800k -b:a:0 96k " +
                        "-b:v:1 1400k -b:a:1 128k " +
                        "-b:v:2 2800k -b:a:2 128k " +
                        "-b:v:3 5000k -b:a:3 192k " +

                        // HLS VOD settings
                        "-f hls " +
                        "-hls_time 15 " +
                        "-hls_playlist_type vod " +
                        "-hls_flags temp_file " +
                        "-start_number 0 " +
                        // multi-variant mapping
                        "-var_stream_map \"v:0,a:0 v:1,a:1 v:2,a:2 v:3,a:3\" " +

                        // output (IMPORTANT)
                        "-hls_segment_filename \"%s/%%v/segment%%03d.ts\" " +
                        "\"%s/%%v/index.m3u8\"",

                filePath,
                outputPath,
                outputPath
        );

        return List.of(command);
    }

}