package com.vsnt.common_lib.dtos.events.live.converted;

import com.vsnt.common_lib.dtos.events.live.LiveEvent;
import com.vsnt.common_lib.dtos.events.live.LiveEventType;

import java.time.Instant;

public class LiveConvertedEvent extends LiveEvent<LiveConvertedPayload> {
    protected LiveConvertedEvent( String liveAssetId, String mediaId, String orgId, Instant timestamp, LiveConvertedPayload data) {
        super(LiveEventType.LIVE_CONVERTED, liveAssetId, mediaId, orgId, timestamp, data);
    }
}
