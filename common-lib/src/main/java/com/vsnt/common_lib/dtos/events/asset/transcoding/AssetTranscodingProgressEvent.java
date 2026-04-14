package com.vsnt.common_lib.dtos.events.asset.transcoding;

import com.vsnt.common_lib.dtos.events.asset.AssetEvent;
import com.vsnt.common_lib.dtos.events.asset.AssetEventType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
@Getter
@Setter
@NoArgsConstructor
public class AssetTranscodingProgressEvent extends AssetEvent<AssetTranscodingProgressPayload> {
    public AssetTranscodingProgressEvent( String assetId, Instant timestamp, AssetTranscodingProgressPayload data) {
        super(AssetEventType.ASSET_TRANSCODING_PROGRESS, assetId, timestamp, data);
    }
}
