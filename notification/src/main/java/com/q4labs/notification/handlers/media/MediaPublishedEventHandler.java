package com.q4labs.notification.handlers.media;

import com.vsnt.common_lib.dtos.events.media.MediaEvent;
import com.vsnt.common_lib.dtos.events.media.MediaEventType;
import com.vsnt.common_lib.dtos.events.media.publish.MediaPublishPayload;
import org.springframework.stereotype.Component;

@Component
public class MediaPublishedEventHandler implements MediaEventHandler<MediaPublishPayload> {
    @Override
    public MediaEventType supports() {
        return MediaEventType.MEDIA_PUBLISHED;
    }

    @Override
    public void handle(MediaEvent<MediaPublishPayload> event) {

    }
}
