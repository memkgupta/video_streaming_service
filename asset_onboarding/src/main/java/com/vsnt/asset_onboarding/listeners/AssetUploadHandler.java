package com.vsnt.asset_onboarding.listeners;

import com.vsnt.asset_onboarding.entities.Asset;
import com.vsnt.asset_onboarding.entities.enums.AssetType;

public interface AssetUploadHandler {
    void handle(Asset asset);
    boolean supports(AssetType assetType);
}
