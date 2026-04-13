package com.vsnt.common_lib.dtos.events.asset.transcoding;

import com.vsnt.common_lib.dtos.events.asset.AssetEvent;
import com.vsnt.common_lib.dtos.events.asset.AssetEventType;

import java.time.Instant;

public class AssetTranscodingFailureEvent extends AssetEvent<AssetTranscodingFailurePayload>{
    protected AssetTranscodingFailureEvent(String assetId, Instant timestamp, AssetTranscodingFailurePayload data) {
        super(AssetEventType.ASSET_TRANSCODING_FAILED, assetId, timestamp, data);
    }
}
