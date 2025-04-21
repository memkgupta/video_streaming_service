package com.vsnt.asset_onboarding.dtos;

import java.util.Map;

public class UploadPauseToggleRequest {
    public UploadPauseToggleRequest() {
    }
    private long assetId;
    private Map<Integer,String> etagMap;

    public long getAssetId() {
        return assetId;
    }

    public void setAssetId(long assetId) {
        this.assetId = assetId;
    }

    public Map<Integer, String> getEtagMap() {
        return etagMap;
    }

    public void setEtagMap(Map<Integer, String> etagMap) {
        this.etagMap = etagMap;
    }
}
