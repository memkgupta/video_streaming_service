package com.vsnt.common_lib.dtos.events.media.view;

import com.vsnt.common_lib.dtos.events.media.MediaEvent;
import com.vsnt.common_lib.dtos.events.media.MediaEventType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
@Getter
@Setter
@NoArgsConstructor
public class MediaViewEvent extends MediaEvent<MediaViewPayload> {
    protected MediaViewEvent( String mediaId, Instant timestamp, String orgId,MediaViewPayload data) {
        super(MediaEventType.MEDIA_VIEWED, mediaId, timestamp, orgId, data);
    }
}
