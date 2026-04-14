package com.q4labs.notification.handlers.asset;

import com.vsnt.common_lib.dtos.events.asset.AssetEvent;
import com.vsnt.common_lib.dtos.events.asset.AssetEventType;
import com.vsnt.common_lib.dtos.events.asset.transcoding.AssetTranscodingCompletedPayload;
import org.springframework.stereotype.Component;

@Component
public class AssetTranscodingCompletedHandler implements  AssetEventHandler<AssetTranscodingCompletedPayload> {
    @Override
    public AssetEventType supports() {
        return AssetEventType.ASSET_TRANSCODING_COMPLETED;
    }

    @Override
    public void handle(AssetEvent<AssetTranscodingCompletedPayload> event) {
        System.out.println("Asset transcoding completed "+ event.getAssetId());
    }
}
