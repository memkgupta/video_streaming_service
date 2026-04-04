package com.vsnt.config;

import java.util.List;

public class FFMPEGConfigVOD extends FFMPEGCommand {

    private final List<String> ffmpegCommands;


    public FFMPEGConfigVOD(String filePath, String outputPath, String encryptionKey) {
        super(filePath, outputPath, encryptionKey);
        ffmpegCommands = List.of(

                // 360p
                String.format(
                        "ffmpeg -i \"%s\" -vf \"scale=w=640:h=360\" " +
                                "-c:v libx264 -preset veryfast -b:v 800k " +
                                "-c:a aac -b:a 96k " +
                                "-f hls -hls_time 15 -hls_playlist_type vod " +
                                "-hls_flags temp_file " +
                                "-hls_segment_filename \"%s/segment%%03d.ts\" " +
                                "-start_number 0 \"%s/index.m3u8\"",
                        filePath,
                        outputPath + "/360p",
                        outputPath + "/360p"
                ),

                // 480p
                String.format(
                        "ffmpeg -i \"%s\" -vf \"scale=w=854:h=480\" " +
                                "-c:v libx264 -preset veryfast -b:v 1400k " +
                                "-c:a aac -b:a 128k " +
                                "-f hls -hls_time 15 -hls_playlist_type vod " +
                                "-hls_flags temp_file " +
                                "-hls_segment_filename \"%s/segment%%03d.ts\" " +
                                "-start_number 0 \"%s/index.m3u8\"",
                        filePath,
                        outputPath + "/480p",
                        outputPath + "/480p"
                ),

                // 720p
                String.format(
                        "ffmpeg -i \"%s\" -vf \"scale=w=1280:h=720\" " +
                                "-c:v libx264 -preset veryfast -b:v 2800k " +
                                "-c:a aac -b:a 128k " +
                                "-f hls -hls_time 15 -hls_playlist_type vod " +
                                "-hls_flags temp_file " +
                                "-hls_segment_filename \"%s/segment%%03d.ts\" " +
                                "-start_number 0 \"%s/index.m3u8\"",
                        filePath,
                        outputPath + "/720p",
                        outputPath + "/720p"
                ),

                // 1080p
                String.format(
                        "ffmpeg -i \"%s\" -vf \"scale=w=1920:h=1080\" " +
                                "-c:v libx264 -preset veryfast -b:v 5000k " +
                                "-c:a aac -b:a 192k " +
                                "-f hls -hls_time 15 -hls_playlist_type vod " +
                                "-hls_flags temp_file " +
                                "-hls_segment_filename \"%s/segment%%03d.ts\" " +
                                "-start_number 0 \"%s/index.m3u8\"",
                        filePath,
                        outputPath + "/1080p",
                        outputPath + "/1080p"
                )
        );


    }

    @Override
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