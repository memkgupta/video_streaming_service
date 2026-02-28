package com.vsnt.asset_onboarding.utils;

import com.vsnt.asset_onboarding.config.Secrets;
import com.vsnt.asset_onboarding.services.S3Service;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
@Component
public class AESKeyGenerator {
    private final S3Service s3Service;

    public AESKeyGenerator(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    public String generateKey(String assetId) throws Exception {
        byte[] key = new byte[16]; // AES-128 requires 16 bytes
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(key);
        String path = "/keys/" + assetId+"_.key";
        s3Service.uploadFileToS3(
                Secrets.AWS_SECURE_BUCKET,
                path , key
        );
        return path;
    }
}