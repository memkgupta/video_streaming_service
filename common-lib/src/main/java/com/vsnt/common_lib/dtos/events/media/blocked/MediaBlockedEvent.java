package com.vsnt.common_lib.dtos.events.media.blocked;

import com.vsnt.common_lib.dtos.events.media.MediaEvent;
import com.vsnt.common_lib.dtos.events.media.MediaEventType;

import java.time.Instant;

public class MediaBlockedEvent extends MediaEvent<MediaBlockedPayload> {
    protected MediaBlockedEvent(String mediaId, Instant timestamp, String orgId, MediaBlockedPayload data) {
        super(MediaEventType.MEDIA_BLOCKED, mediaId, timestamp, orgId, data);
    }
}
