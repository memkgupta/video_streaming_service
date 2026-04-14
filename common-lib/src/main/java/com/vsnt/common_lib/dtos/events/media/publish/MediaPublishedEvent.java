package com.vsnt.common_lib.dtos.events.media.publish;

import com.vsnt.common_lib.dtos.events.media.MediaEvent;
import com.vsnt.common_lib.dtos.events.media.MediaEventType;

import java.time.Instant;

public class MediaPublishedEvent extends MediaEvent<MediaPublishPayload> {
    public MediaPublishedEvent(String mediaId, Instant timestamp, String orgId,MediaPublishPayload data) {
        super(MediaEventType.MEDIA_PUBLISHED, mediaId, timestamp, orgId,data);
    }
}
