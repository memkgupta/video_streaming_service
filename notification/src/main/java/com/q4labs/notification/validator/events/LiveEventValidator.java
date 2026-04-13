package com.q4labs.notification.validator.events;

import com.vsnt.common_lib.dtos.events.live.LiveEvent;

import com.vsnt.common_lib.dtos.events.live.end.LiveEndPayload;
import com.vsnt.common_lib.dtos.events.live.start.LiveStartedPayload;

import com.vsnt.common_lib.dtos.events.live.converted.LiveConvertedPayload;

public class LiveEventValidator {

    public static LiveEvent<?> validate(LiveEvent<?> eventReceived) {

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

            case LIVE_STARTED -> {
                validateType(eventReceived, LiveStartedPayload.class);
            }

            case LIVE_ENDED -> {
                validateType(eventReceived, LiveEndPayload.class);
            }

            case LIVE_CONVERTED -> {
                validateType(eventReceived, LiveConvertedPayload.class);
            }

            default -> throw new IllegalArgumentException(
                    "Unsupported event type: " + eventReceived.getEventType()
            );
        }

        return eventReceived;
    }

    private static void validateType(LiveEvent<?> event, Class<?> expectedClass) {
        if (!expectedClass.isInstance(event.getData())) {
            throw new IllegalArgumentException(
                    "Invalid payload type for event " + event.getEventType() +
                            ". Expected: " + expectedClass.getSimpleName() +
                            ", Found: " + event.getData().getClass().getSimpleName()
            );
        }
    }
}