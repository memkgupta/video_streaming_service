package com.vsnt.common_lib.dtos.events.media.processing;

import com.vsnt.common_lib.dtos.events.media.MediaEvent;
import com.vsnt.common_lib.dtos.events.media.MediaEventType;
import com.vsnt.common_lib.dtos.events.media.publish.MediaPublishPayload;

import java.time.Instant;

public class MediaProcessingEvent extends MediaEvent<MediaPublishPayload> {
    protected MediaProcessingEvent( String mediaId, Instant timestamp,String orgId, MediaPublishPayload data) {
        super(MediaEventType.MEDIA_PROCESSING, mediaId, timestamp ,orgId, data);
    }
}
