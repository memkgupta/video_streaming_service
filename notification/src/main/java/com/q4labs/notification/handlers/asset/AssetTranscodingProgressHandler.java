package com.q4labs.notification.handlers.asset;

import com.vsnt.common_lib.dtos.events.asset.AssetEvent;
import com.vsnt.common_lib.dtos.events.asset.AssetEventType;
import com.vsnt.common_lib.dtos.events.asset.transcoding.AssetTranscodingCompletedPayload;
import com.vsnt.common_lib.dtos.events.asset.transcoding.AssetTranscodingProgressPayload;

public class AssetTranscodingProgressHandler implements AssetEventHandler<AssetTranscodingProgressPayload> {
    @Override
    public AssetEventType supports() {
        return AssetEventType.ASSET_TRANSCODING_PROGRESS;
    }

    @Override
    public void handle(AssetEvent<AssetTranscodingProgressPayload> event) {
        System.out.println("Asset transcoding completed "+ event.getAssetId()+ "-> "+event.getData().getProgressPercentage());
    }
}
