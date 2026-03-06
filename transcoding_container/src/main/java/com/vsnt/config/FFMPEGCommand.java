package com.vsnt.config;

import java.util.List;

public abstract class FFMPEGCommand {
    protected final String filePath;
    protected final String outputPath;
    protected final String encryptionKey;
    protected FFMPEGCommand(String filePath , String outputPath , String encryptionKey) {

        this.filePath = filePath;
        this.outputPath = outputPath;
        this.encryptionKey = encryptionKey;
    }
    public abstract List<String> getFFMPEGCommands();
}
