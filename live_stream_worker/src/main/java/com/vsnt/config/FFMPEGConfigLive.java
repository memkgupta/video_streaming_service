package com.vsnt.config;

import java.util.List;

public class FFMPEGConfigLive {
    private String url;
    private String outputPath;
    private String encryptionKey;
    public FFMPEGConfigLive(String url, String outputPath, String encryptionKey) {
       this.url = url;
       this.outputPath = outputPath;
       this.encryptionKey = encryptionKey;
    }


    public List<String> getFFMPEGCommands() {
        return List.of(
                buildAdaptiveHLSCommand()

        );
    }

    //  Existing ABR HLS command
    private String buildAdaptiveHLSCommand() {
        return String.format(
                "ffmpeg -i \"%s\" " +
                        "-filter_complex \"" +
                        "[0:v]split=4[v1][v2][v3][v4];" +
                        "[v1]scale=640:360[v360];" +
                        "[v2]scale=854:480[v480];" +
                        "[v3]scale=1280:720[v720];" +
                        "[v4]scale=1920:1080[v1080]" +
                        "\" " +

                        "-map \"[v360]\" -map 0:a " +
                        "-map \"[v480]\" -map 0:a " +
                        "-map \"[v720]\" -map 0:a " +
                        "-map \"[v1080]\" -map 0:a " +

                        "-c:v libx264 -preset veryfast " +
                        "-c:a aac " +

                        "-f hls -hls_time 4 -hls_list_size 6 " +
                        "-hls_flags temp_file+append_list " +

                        "-var_stream_map \"v:0,a:0 v:1,a:1 v:2,a:2 v:3,a:3\" " +
                        "-hls_segment_filename \"%s/%%v/segment%%03d.ts\" " +
                        "%s/%%v/index.m3u8",

                url,
                outputPath,
                outputPath
        );
    }

    //  RTMP → MP4 segmentation For Moderation purpose
    public String buildMP4SegmentCommand() {
        return String.format(
                "ffmpeg -i \"%s\" " +
                        "-c copy " +
                        "-map 0:v -map 0:a? " +                 // safer mapping
                        "-f  " +
                        "-segment_time 4 " +
                        "-reset_timestamps 1 " +
                        "-segment_format mp4 " +
                        "-segment_format_options movflags=+faststart " +
                        "-strftime 1 " +                        // 🔥 timestamp-based naming
                        "-segment_list \"%s/segments.txt\" " +  // optional index file
                        "\"%s/segment_%%Y%%m%%d_%%H%%M%%S.mp4\"",

                url,
                outputPath,
                outputPath
        );
    }



}
