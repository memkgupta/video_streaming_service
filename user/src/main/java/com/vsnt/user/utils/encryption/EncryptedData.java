package com.vsnt.user.utils.encryption;

public  class EncryptedData {

    private final String iv;
    private final String ciphertext;

    public EncryptedData(
            String iv,
            String ciphertext
    ) {
        this.iv = iv;
        this.ciphertext = ciphertext;
    }

    public String getIv() {
        return iv;
    }

    public String getCiphertext() {
        return ciphertext;
    }
}
