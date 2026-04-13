package com.vsnt.common_lib.dtos.events.media;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
public abstract class MediaEvent<T> {
    protected final MediaEventType eventType;
    protected final String mediaId;
    protected final Instant timestamp;
    protected final String orgId;
    protected final T data;
    protected MediaEvent(MediaEventType eventType, String mediaId, Instant timestamp, String orgId, T data) {
        this.eventType = eventType;
        this.mediaId = mediaId;
        this.timestamp = timestamp;
        this.orgId = orgId;
        this.data = data;
    }


}
