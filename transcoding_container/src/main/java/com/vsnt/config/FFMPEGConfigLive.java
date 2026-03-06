package com.vsnt.config;

import java.util.List;

public class FFMPEGConfigLive extends FFMPEGCommand{
    protected FFMPEGConfigLive(String filePath, String outputPath, String encryptionKey) {
        super(filePath, outputPath, encryptionKey);
    }

    @Override
    public List<String> getFFMPEGCommands() {
        return List.of(

                // 360p
                String.format(
                        "ffmpeg -i \"%s\" -vf \"scale=w=640:h=360\" " +
                                "-c:v libx264 -preset veryfast -b:v 800k " +
                                "-c:a aac -b:a 96k " +
                                "-f hls -hls_time 4 -hls_list_size 6 " +
//                                "-hls_flags delete_segments+append_list " +
                                "-hls_segment_filename \"%s/360p/segment%%03d.ts\" " +
                                "\"%s/360p/index.m3u8\"",
                        filePath, outputPath, outputPath
                ),

                // 480p
                String.format(
                        "ffmpeg -i \"%s\" -vf \"scale=w=854:h=480\" " +
                                "-c:v libx264 -preset veryfast -b:v 1400k " +
                                "-c:a aac -b:a 128k " +
                                "-f hls -hls_time 4 -hls_list_size 6 " +
//                                "-hls_flags delete_segments+append_list " +
                                "-hls_segment_filename \"%s/480p/segment%%03d.ts\" " +
                                "\"%s/480p/index.m3u8\"",
                        filePath, outputPath, outputPath
                ),

                // 720p
                String.format(
                        "ffmpeg -i \"%s\" -vf \"scale=w=1280:h=720\" " +
                                "-c:v libx264 -preset veryfast -b:v 2800k " +
                                "-c:a aac -b:a 128k " +
                                "-f hls -hls_time 4 -hls_list_size 6 " +
//                                "-hls_flags delete_segments+append_list " +
                                "-hls_segment_filename \"%s/720p/segment%%03d.ts\" " +
                                "\"%s/720p/index.m3u8\"",
                        filePath, outputPath, outputPath
                ),

                // 1080p
                String.format(
                        "ffmpeg -i \"%s\" -vf \"scale=w=1920:h=1080\" " +
                                "-c:v libx264 -preset veryfast -b:v 5000k " +
                                "-c:a aac -b:a 192k " +
                                "-f hls -hls_time 4 -hls_list_size 6 " +
//                                "-hls_flags delete_segments+append_list " +
                                "-hls_segment_filename \"%s/1080p/segment%%03d.ts\" " +
                                "\"%s/1080p/index.m3u8\"",
                        filePath, outputPath, outputPath
                )
        );
    }
}
