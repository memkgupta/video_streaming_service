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
public class AssetTranscodingCompletedEvent extends AssetEvent<AssetTranscodingCompletedPayload> {
    public AssetTranscodingCompletedEvent( String assetId, Instant timestamp, AssetTranscodingCompletedPayload data) {
        super(AssetEventType.ASSET_TRANSCODING_COMPLETED, assetId, timestamp, data);
    }
}
