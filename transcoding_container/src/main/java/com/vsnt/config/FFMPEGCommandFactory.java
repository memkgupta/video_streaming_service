package com.vsnt.config;

import com.vsnt.dtos.MediaType;

public class FFMPEGCommandFactory {
    public static FFMPEGCommand getFFMPEGCommand(MediaType mediaType, String filePath, String outputPath, String encryptionKey) {
        if(mediaType == MediaType.LIVE)
        {
            return new FFMPEGConfigLive(filePath, outputPath, encryptionKey);
        }
        else {
            return new FFMPEGConfigVOD(filePath, outputPath, encryptionKey);
        }
    }
}
