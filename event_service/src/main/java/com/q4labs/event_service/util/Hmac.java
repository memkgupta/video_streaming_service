package com.q4labs.event_service.util;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.HexFormat;

public class Hmac {
    public static String generateHmac(
            String payload,
            String secret
    ) throws Exception {

        // Create HMAC SHA256 instance
        Mac mac = Mac.getInstance("HmacSHA256");

        // Convert secret into cryptographic key
        SecretKeySpec secretKey =
                new SecretKeySpec(
                        secret.getBytes(),
                        "HmacSHA256"
                );

        // Initialize with secret key
        mac.init(secretKey);

        // Generate HMAC bytes
        byte[] hmacBytes =
                mac.doFinal(payload.getBytes());

        // Convert bytes to hex string
        return HexFormat.of().formatHex(hmacBytes);
    }



}

