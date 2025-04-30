package com.vsnt.config;

import java.util.ArrayList;
import java.util.List;

public class FFMPEGConfig {
    private List<String> ffmpegCommands;
    private String masterPlaylistContent;
    public FFMPEGConfig(String filePath,String outputPath) {
        ffmpegCommands = List.of(
                String.format(
                        "ffmpeg -i \"%s\" -vf \"scale=w=640:h=360\" -c:v libx264 -b:v 800k -c:a aac -b:a 96k -f hls -hls_time 15 -hls_playlist_type vod -hls_segment_filename \"%s/segment%%03d.ts\" -start_number 0 \"%s/index.m3u8\"",
                        filePath,
                        outputPath+"/"+"360p",
                        outputPath+"/"+"360p"
                ),
                // 480p resolution
                String.format(
                        "ffmpeg -i \"%s\" -vf \"scale=w=854:h=480\" -c:v libx264 -b:v 1400k -c:a aac -b:a 128k -f hls -hls_time 15 -hls_playlist_type vod -hls_segment_filename \"%s/segment%%03d.ts\" -start_number 0 \"%s/index.m3u8\"",
                        filePath,
                        outputPath+"/"+"480p",
                        outputPath+"/"+"480p"
                ),
                // 720p resolution
                String.format(
                        "ffmpeg -i \"%s\" -vf \"scale=w=1280:h=720\" -c:v libx264 -b:v 2800k -c:a aac -b:a 128k -f hls -hls_time 15 -hls_playlist_type vod -hls_segment_filename \"%s/segment%%03d.ts\" -start_number 0 \"%s/index.m3u8\"",
                        filePath,
                        outputPath+"/"+"720p",
                        outputPath+"/"+"720p"
                ),
                // 1080p resolution
                String.format(
                        "ffmpeg -i \"%s\" -vf \"scale=w=1920:h=1080\" -c:v libx264 -b:v 5000k -c:a aac -b:a 192k -f hls -hls_time 15 -hls_playlist_type vod -hls_segment_filename \"%s/segment%%03d.ts\" -start_number 0 \"%s/index.m3u8\"",
                        filePath,
                        outputPath+"/"+"1080p",
                        outputPath+"/"+"1080p"
                )
        );

      masterPlaylistContent =   """
                #EXTM3U
                #EXT-X-STREAM-INF:BANDWIDTH=800000,RESOLUTION=640x360
                360p/index.m3u8
                #EXT-X-STREAM-INF:BANDWIDTH=1400000,RESOLUTION=854x480
                480p/index.m3u8
                #EXT-X-STREAM-INF:BANDWIDTH=2800000,RESOLUTION=1280x720
                720p/index.m3u8
                #EXT-X-STREAM-INF:BANDWIDTH=5000000,RESOLUTION=1920x1080
                1080p/index.m3u8
                """;
    }

    public List<String> getFfmpegCommands() {
        return ffmpegCommands;
    }

    public void setFfmpegCommands(List<String> ffmpegCommands) {
        this.ffmpegCommands = ffmpegCommands;
    }

    public String getMasterPlaylistContent() {
        return masterPlaylistContent;
    }

    public void setMasterPlaylistContent(String masterPlaylistContent) {
        this.masterPlaylistContent = masterPlaylistContent;
    }
}
