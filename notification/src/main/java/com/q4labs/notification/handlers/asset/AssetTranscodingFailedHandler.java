package com.q4labs.notification.handlers.asset;

import com.vsnt.common_lib.dtos.events.asset.AssetEvent;
import com.vsnt.common_lib.dtos.events.asset.AssetEventType;
import com.vsnt.common_lib.dtos.events.asset.transcoding.AssetTranscodingFailurePayload;
import org.springframework.stereotype.Component;

@Component
public class AssetTranscodingFailedHandler implements AssetEventHandler<AssetTranscodingFailurePayload> {
    @Override
    public AssetEventType supports() {
        return AssetEventType.ASSET_TRANSCODING_FAILED;
    }

    @Override
    public void handle(AssetEvent<AssetTranscodingFailurePayload> event) {

    }
}
