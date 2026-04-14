package com.vsnt.common_lib.dtos.events.media.processing;

import com.vsnt.common_lib.dtos.events.media.MediaEvent;
import com.vsnt.common_lib.dtos.events.media.MediaEventType;
import com.vsnt.common_lib.dtos.events.media.publish.MediaPublishPayload;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
@Getter
@Setter
@NoArgsConstructor
public class MediaProcessingEvent extends MediaEvent<MediaProcessingPayload> {
    public MediaProcessingEvent( String mediaId, Instant timestamp,String orgId, MediaProcessingPayload data) {
        super(MediaEventType.MEDIA_PROCESSING, mediaId, timestamp ,orgId, data);
    }
}
