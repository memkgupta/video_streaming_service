package com.vsnt.common_lib.dtos.events.media.blocked;

import com.vsnt.common_lib.dtos.events.media.MediaEvent;
import com.vsnt.common_lib.dtos.events.media.MediaEventType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
@Getter
@Setter
@NoArgsConstructor
public class MediaBlockedEvent extends MediaEvent<MediaBlockedPayload> {
    public MediaBlockedEvent(String mediaId, Instant timestamp, String orgId, MediaBlockedPayload data) {
        super(MediaEventType.MEDIA_BLOCKED, mediaId, timestamp, orgId, data);
    }
}
