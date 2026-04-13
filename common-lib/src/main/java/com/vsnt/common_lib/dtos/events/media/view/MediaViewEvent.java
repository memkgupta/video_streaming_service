package com.vsnt.common_lib.dtos.events.media.view;

import com.vsnt.common_lib.dtos.events.media.MediaEvent;
import com.vsnt.common_lib.dtos.events.media.MediaEventType;

import java.time.Instant;

public class MediaViewEvent extends MediaEvent<MediaViewPayload> {
    protected MediaViewEvent( String mediaId, Instant timestamp, String orgId,MediaViewPayload data) {
        super(MediaEventType.MEDIA_VIEWED, mediaId, timestamp, orgId, data);
    }
}
