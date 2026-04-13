package com.vsnt.common_lib.dtos.events.live;
import lombok.Getter;
import java.time.Instant;

@Getter
public abstract class LiveEvent<T> {
    private final String mediaId;
    private final String orgId;
    private final LiveEventType eventType;
    private final String liveAssetId;
    protected final Instant timestamp;
    protected final T data;

    protected LiveEvent(LiveEventType eventType, String liveAssetId, String mediaId , String orgId, Instant timestamp, T data) {
        this.liveAssetId = liveAssetId;
        this.mediaId = mediaId;
        this.orgId = orgId;
        this.eventType = eventType;
        this.data = data;
        this.timestamp = timestamp;
    }
}
