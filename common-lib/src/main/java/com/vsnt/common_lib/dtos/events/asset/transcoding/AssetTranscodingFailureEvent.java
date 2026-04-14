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
public class AssetTranscodingFailureEvent extends AssetEvent<AssetTranscodingFailurePayload>{
    public AssetTranscodingFailureEvent(String assetId, Instant timestamp, AssetTranscodingFailurePayload data) {
        super(AssetEventType.ASSET_TRANSCODING_FAILED, assetId, timestamp, data);
    }
}
