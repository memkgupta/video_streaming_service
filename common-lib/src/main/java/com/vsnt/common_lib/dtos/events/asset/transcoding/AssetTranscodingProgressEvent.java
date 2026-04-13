package com.vsnt.common_lib.dtos.events.asset.transcoding;

import com.vsnt.common_lib.dtos.events.asset.AssetEvent;
import com.vsnt.common_lib.dtos.events.asset.AssetEventType;

import java.time.Instant;

public class AssetTranscodingProgressEvent extends AssetEvent<AssetTranscodingProgressPayload> {
    protected AssetTranscodingProgressEvent( String assetId, Instant timestamp, AssetTranscodingProgressPayload data) {
        super(AssetEventType.ASSET_TRANSCODING_PROGRESS, assetId, timestamp, data);
    }
}
