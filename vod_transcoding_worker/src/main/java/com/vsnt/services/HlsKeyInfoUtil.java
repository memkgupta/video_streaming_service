package com.vsnt.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class HlsKeyInfoUtil {

    public static Path createKeyInfoFile(String publicKeyUrl,
                                         Path keyFilePath,
                                         String hexKey,
                                         String outputDir) throws IOException, IOException {

        Path keyInfoPath = Path.of(outputDir, "key_info.txt");

        String content =
                publicKeyUrl + "\n" +
                        keyFilePath.toAbsolutePath() + "\n" +
                        hexKey;

        Files.writeString(keyInfoPath, content);

        return keyInfoPath;
    }
}