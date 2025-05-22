package com.vsnt.asset_onboarding.dtos;

import lombok.Data;

@Data
public class ChunkUploadRequest {
    private String key;
    private String uploadId;
    private int partNumber;
    private long assetId;

    public long getAssetId() {
        return assetId;
    }

    @Override
    public String toString() {
        return "ChunkUploadRequest{" +
                "key='" + key + '\'' +
                ", uploadId='" + uploadId + '\'' +
                ", partNumber=" + partNumber +
                ", assetId=" + assetId +
                '}';
    }

    public void setAssetId(long assetId) {
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

    public int getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(int partNumber) {
        this.partNumber = partNumber;
    }

}
