package com.vsnt.common_lib.dtos.events.live.end;

import com.vsnt.common_lib.dtos.events.live.LiveEvent;
import com.vsnt.common_lib.dtos.events.live.LiveEventType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
@Getter
@Setter
@NoArgsConstructor
public class LiveEndEvent extends LiveEvent<LiveEndPayload> {
    public LiveEndEvent( String liveAssetId, String mediaId, String orgId, Instant timestamp, LiveEndPayload data) {
        super(LiveEventType.LIVE_ENDED, liveAssetId, mediaId, orgId, timestamp, data);
    }
}
