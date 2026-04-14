package com.vsnt.common_lib.dtos.events.media;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.vsnt.common_lib.dtos.events.live.converted.LiveConvertedEvent;
import com.vsnt.common_lib.dtos.events.live.end.LiveEndEvent;
import com.vsnt.common_lib.dtos.events.live.start.LiveStartedEvent;
import com.vsnt.common_lib.dtos.events.media.blocked.MediaBlockedEvent;
import com.vsnt.common_lib.dtos.events.media.processing.MediaProcessingEvent;
import com.vsnt.common_lib.dtos.events.media.publish.MediaPublishedEvent;
import com.vsnt.common_lib.dtos.events.media.review.MediaUnderReviewEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
@NoArgsConstructor
@JsonTypeInfo(  use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "eventType",
        visible = true )
@JsonSubTypes({
        @JsonSubTypes.Type(value = MediaProcessingEvent.class,name = "MEDIA_PROCESSING"),
        @JsonSubTypes.Type(value = MediaBlockedEvent.class,name = "MEDIA_BLOCKED"),
        @JsonSubTypes.Type(value = MediaPublishedEvent.class,name = "MEDIA_PUBLISHED"),
        @JsonSubTypes.Type(value = MediaUnderReviewEvent.class,name = "MEDIA_UNDER_REVIEW"),
})
public abstract class MediaEvent<T> {
    protected  MediaEventType eventType;
    protected  String mediaId;
    protected  Instant timestamp;
    protected  String orgId;
    protected  T data;
    protected MediaEvent(MediaEventType eventType, String mediaId, Instant timestamp, String orgId, T data) {
        this.eventType = eventType;
        this.mediaId = mediaId;
        this.timestamp = timestamp;
        this.orgId = orgId;
        this.data = data;
    }


}
