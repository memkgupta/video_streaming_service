package com.q4labs.notification.handlers.media;

import com.vsnt.common_lib.dtos.events.media.MediaEvent;
import com.vsnt.common_lib.dtos.events.media.MediaEventType;
import com.vsnt.common_lib.dtos.events.media.processing.MediaProcessingPayload;
import org.springframework.stereotype.Component;

@Component
public class MediaProcessingEventHandler implements MediaEventHandler<MediaProcessingPayload> {
    @Override
    public MediaEventType supports() {
        return MediaEventType.MEDIA_PROCESSING;
    }

    @Override
    public void handle(MediaEvent<MediaProcessingPayload> event) {

    }
}
