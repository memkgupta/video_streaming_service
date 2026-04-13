package com.q4labs.notification.handlers.media;

import com.vsnt.common_lib.dtos.events.media.MediaEvent;
import com.vsnt.common_lib.dtos.events.media.MediaEventType;
import com.vsnt.common_lib.dtos.events.media.blocked.MediaBlockedPayload;
import org.springframework.stereotype.Component;

@Component
public class MediaBlockedEventHandler implements MediaEventHandler<MediaBlockedPayload> {
    @Override
    public MediaEventType supports() {
        return MediaEventType.MEDIA_BLOCKED;
    }

    @Override
    public void handle(MediaEvent<MediaBlockedPayload> event) {

    }
}
