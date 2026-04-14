package com.vsnt.common_lib.dtos.events.asset;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.vsnt.common_lib.dtos.events.asset.transcoding.AssetTranscodingCompletedEvent;
import com.vsnt.common_lib.dtos.events.asset.transcoding.AssetTranscodingFailureEvent;
import com.vsnt.common_lib.dtos.events.asset.transcoding.AssetTranscodingProgressEvent;
import com.vsnt.common_lib.dtos.events.live.converted.LiveConvertedEvent;
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
        @JsonSubTypes.Type(value = AssetTranscodingProgressEvent.class,name = "ASSET_TRANSCODING_PROGRESS"),
        @JsonSubTypes.Type(value = AssetTranscodingCompletedEvent.class,name = "ASSET_TRANSCODING_COMPLETED"),
        @JsonSubTypes.Type(value = AssetTranscodingFailureEvent.class,name = "ASSET_TRANSCODING_FAILED")
})
public abstract class AssetEvent<T> {
    private  AssetEventType eventType;
    protected  String assetId;
    protected  Instant timestamp;
    protected  T data;

    protected AssetEvent(AssetEventType eventType, String assetId, Instant timestamp, T data) {
        this.eventType = eventType;
        this.assetId = assetId;
        this.timestamp = timestamp;
        this.data = data;
    }
}
