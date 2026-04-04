package com.vsnt.asset_onboarding.dtos;

import lombok.Data;

@Data
public class ChunkUploadRequest {

    private String uploadId;
    private int partNumber;
    private long assetId;

    public long getAssetId() {
        return assetId;
    }

    @Override
    public String toString() {
        return "ChunkUploadRequest{" +

                ", uploadId='" + uploadId + '\'' +
                ", partNumber=" + partNumber +
                ", assetId=" + assetId +
                '}';
    }

    public void setAssetId(long assetId) {
        this.assetId = assetId;
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
