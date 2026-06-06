package com.vsnt.user.utils.encryption;


import org.springframework.stereotype.Component;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class AESEncryptor implements WebhookSecretUtil {
    private static final String AES = "AES";
    private static final String AES_GCM_NO_PADDING =
            "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 128;

    private static final int IV_LENGTH = 12;
    private static final SecureRandom secureRandom =
            new SecureRandom();
    private final SecretKey secretKey;

    public AESEncryptor() {
        String base64Key =
                System.getenv("WEBHOOK_MASTER_KEY");

        byte[] decodedKey =
                Base64.getDecoder()
                        .decode(base64Key);

         secretKey =
                new SecretKeySpec(
                        decodedKey,
                        "AES"
                );
    }

    @Override
    public EncryptedData encrypt(String plaintext) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        byte[] iv = new byte[IV_LENGTH];

        SecureRandom secureRandom =
                new SecureRandom();

        secureRandom.nextBytes(iv);

        Cipher cipher =
                Cipher.getInstance(AES_GCM_NO_PADDING);

        GCMParameterSpec spec =
                new GCMParameterSpec(
                        GCM_TAG_LENGTH,
                        iv
                );

        cipher.init(
                Cipher.ENCRYPT_MODE,
                secretKey,
                spec
        );

        byte[] encryptedBytes =
                cipher.doFinal(
                        plaintext.getBytes(
                                StandardCharsets.UTF_8
                        )
                );

        return new EncryptedData(
                Base64.getEncoder()
                        .encodeToString(iv),

                Base64.getEncoder()
                        .encodeToString(encryptedBytes)
        );
    }



    @Override
    public  String decrypt(
            String ivBase64,
            String encryptedBase64
    ) throws IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {

        byte[] iv =
                Base64.getDecoder()
                        .decode(ivBase64);

        byte[] encryptedBytes =
                Base64.getDecoder()
                        .decode(encryptedBase64);

        Cipher cipher =
                Cipher.getInstance(AES_GCM_NO_PADDING);

        GCMParameterSpec spec =
                new GCMParameterSpec(
                        GCM_TAG_LENGTH,
                        iv
                );

        cipher.init(
                Cipher.DECRYPT_MODE,
                secretKey,
                spec
        );

        byte[] decryptedBytes =
                cipher.doFinal(encryptedBytes);

        return new String(
                decryptedBytes,
                StandardCharsets.UTF_8
        );
    }

    @Override
    public String generateSecret() {
        byte[] randomBytes = new byte[32];

        secureRandom.nextBytes(randomBytes);

        return "whsec_" +
                Base64.getUrlEncoder()
                        .withoutPadding()
                        .encodeToString(randomBytes);
    }
}
