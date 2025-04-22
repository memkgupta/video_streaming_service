package com.vsnt.asset_onboarding.dtos;

import lombok.Data;

import java.util.Map;

@Data
public class FinalizeUploadRequest {
    private String uploadId;
    private String key;
    private Map<Integer,String> etagMap;
    private long assetId;

    public FinalizeUploadRequest() {
    }

    public long getAssetId() {
        return assetId;
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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Map<Integer, String> getEtagMap() {
        return etagMap;
    }

    public void setEtagMap(Map<Integer, String> etagMap) {
        this.etagMap = etagMap;
    }
}
