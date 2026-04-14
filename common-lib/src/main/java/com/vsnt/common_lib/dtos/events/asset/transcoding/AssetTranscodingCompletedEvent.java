package com.vsnt.common_lib.dtos.events.asset.transcoding;

import com.vsnt.common_lib.dtos.events.asset.AssetEvent;
import com.vsnt.common_lib.dtos.events.asset.AssetEventType;

import java.time.Instant;

public class AssetTranscodingCompletedEvent extends AssetEvent<AssetTranscodingCompletedPayload> {
    public AssetTranscodingCompletedEvent( String assetId, Instant timestamp, AssetTranscodingCompletedPayload data) {
        super(AssetEventType.ASSET_PROCESSING_COMPLETED, assetId, timestamp, data);
    }
}
