package com.q4labs.notification.validator.events;

import com.vsnt.common_lib.dtos.events.media.MediaEvent;
import com.vsnt.common_lib.dtos.events.media.blocked.MediaBlockedPayload;
import com.vsnt.common_lib.dtos.events.media.processing.MediaProcessingPayload;
import com.vsnt.common_lib.dtos.events.media.publish.MediaPublishPayload;
import com.vsnt.common_lib.dtos.events.media.review.MediaReviewPayload;


public class MediaEventValidator {

    public static MediaEvent<?> validate(MediaEvent<?> eventReceived) {

        if (eventReceived == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }

        if (eventReceived.getEventType() == null) {
            throw new IllegalArgumentException("Event type cannot be null");
        }

        if (eventReceived.getData() == null) {
            throw new IllegalArgumentException("Event data cannot be null");
        }

        switch (eventReceived.getEventType()) {

            case MEDIA_BLOCKED -> {
                validateType(eventReceived, MediaBlockedPayload.class);
            }

            case MEDIA_PROCESSING -> {
                validateType(eventReceived, MediaProcessingPayload.class);
            }

            case MEDIA_PUBLISHED -> {
                validateType(eventReceived, MediaPublishPayload.class);
            }

            case MEDIA_UNDER_REVIEW -> {
                validateType(eventReceived, MediaReviewPayload.class);
            }

            default -> throw new IllegalArgumentException(
                    "Unsupported event type: " + eventReceived.getEventType()
            );
        }

        return eventReceived;
    }

    private static void validateType(MediaEvent<?> event, Class<?> expectedClass) {
        if (!expectedClass.isInstance(event.getData())) {
            throw new IllegalArgumentException(
                    "Invalid payload for event " + event.getEventType() +
                            ". Expected: " + expectedClass.getSimpleName() +
                            ", Found: " + event.getData().getClass().getSimpleName()
            );
        }
    }
}