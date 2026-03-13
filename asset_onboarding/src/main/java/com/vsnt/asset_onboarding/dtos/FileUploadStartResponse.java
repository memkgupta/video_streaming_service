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
    private String pushKey;

}
