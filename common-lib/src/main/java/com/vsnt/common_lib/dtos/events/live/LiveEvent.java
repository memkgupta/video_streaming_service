package com.vsnt.common_lib.dtos.events.live;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.vsnt.common_lib.dtos.events.live.converted.LiveConvertedEvent;
import com.vsnt.common_lib.dtos.events.live.converted.LiveConvertedPayload;
import com.vsnt.common_lib.dtos.events.live.end.LiveEndEvent;
import com.vsnt.common_lib.dtos.events.live.start.LiveStartedEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@JsonTypeInfo(  use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "eventType",
        visible = true )
@JsonSubTypes({
        @JsonSubTypes.Type(value = LiveStartedEvent.class,name = "LIVE_STARTED"),
        @JsonSubTypes.Type(value = LiveEndEvent.class,name = "LIVE_ENDED"),
        @JsonSubTypes.Type(value = LiveConvertedEvent.class,name = "LIVE_CONVERTED")
})
public abstract class LiveEvent<T> {
    private  String mediaId;
    private  String orgId;
    private  LiveEventType eventType;
    private  String liveAssetId;
    protected  Instant timestamp;
    protected  T data;

    protected LiveEvent(LiveEventType eventType, String liveAssetId, String mediaId , String orgId, Instant timestamp, T data) {
        this.liveAssetId = liveAssetId;
        this.mediaId = mediaId;
        this.orgId = orgId;
        this.eventType = eventType;
        this.data = data;
        this.timestamp = timestamp;
    }
}
