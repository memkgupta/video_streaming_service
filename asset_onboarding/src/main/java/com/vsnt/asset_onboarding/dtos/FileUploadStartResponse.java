package com.vsnt.asset_onboarding.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

public class FileUploadStartResponse {
    public FileUploadStartResponse() {}
    public FileUploadStartResponse(String key, String uploadId) {
        this.key = key;
        this.uploadId = uploadId;
    }

    private String key;
    private String uploadId;
    private String assetId;

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUploadId() {
        return uploadId;
    }

    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }
}
