package com.vsnt.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class HlsKeyUtil {

    public static Path createKeyFile(String hexKey, String outputDir) throws IOException {

        if (hexKey == null || hexKey.length() != 32) {
            throw new IllegalArgumentException("AES-128 key must be 32 hex characters (16 bytes)");
        }

        byte[] keyBytes = hexToBytes(hexKey);

        Path keyPath = Path.of(outputDir, "enc.key");
        Files.write(keyPath, keyBytes);

        return keyPath;
    }

    private static byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];

        for (int i = 0; i < len; i += 2) {
            data[i / 2] =
                    (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                            + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }
}