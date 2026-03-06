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

    public List<String> getFFMPEGCommands() {
        return ffmpegCommands;
    }


}