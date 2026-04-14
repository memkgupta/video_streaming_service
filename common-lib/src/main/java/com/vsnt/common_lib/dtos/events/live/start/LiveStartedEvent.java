package com.vsnt.common_lib.dtos.events.live.start;

import com.vsnt.common_lib.dtos.events.live.LiveEvent;
import com.vsnt.common_lib.dtos.events.live.LiveEventType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
@Getter
@Setter
@NoArgsConstructor
public class LiveStartedEvent extends LiveEvent<LiveStartedPayload> {
    public LiveStartedEvent( String liveAssetId, String mediaId, String orgId, Instant timestamp, LiveStartedPayload data) {
        super(LiveEventType.LIVE_STARTED, liveAssetId, mediaId, orgId, timestamp, data);
    }
}
