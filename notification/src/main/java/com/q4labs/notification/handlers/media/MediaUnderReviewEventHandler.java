package com.q4labs.notification.handlers.media;

import com.vsnt.common_lib.dtos.events.media.MediaEvent;
import com.vsnt.common_lib.dtos.events.media.MediaEventType;
import com.vsnt.common_lib.dtos.events.media.review.MediaReviewPayload;
import org.springframework.stereotype.Component;

@Component
public class MediaUnderReviewEventHandler implements MediaEventHandler<MediaReviewPayload> {
    @Override
    public MediaEventType supports() {
        return MediaEventType.MEDIA_UNDER_REVIEW;
    }

    @Override
    public void handle(MediaEvent<MediaReviewPayload> event) {

    }
}
