package com.q4labs.notification.validator.events;

import com.vsnt.common_lib.dtos.events.asset.AssetEvent;
import com.vsnt.common_lib.dtos.events.asset.transcoding.AssetTranscodingCompletedEvent;
import com.vsnt.common_lib.dtos.events.asset.transcoding.AssetTranscodingCompletedPayload;
import com.vsnt.common_lib.dtos.events.asset.transcoding.AssetTranscodingFailurePayload;
import com.vsnt.common_lib.dtos.events.asset.transcoding.AssetTranscodingProgressPayload;
public class AssetEventValidator {

    public static AssetEvent<?> validate(AssetEvent<?> event) {

        switch (event.getEventType()) {

            case ASSET_TRANSCODING_COMPLETED -> {
                validateType(event, AssetTranscodingCompletedPayload.class);
            }

            case ASSET_TRANSCODING_FAILED -> {
                validateType(event, AssetTranscodingFailurePayload.class);
            }

            case ASSET_TRANSCODING_PROGRESS -> {
                validateType(event, AssetTranscodingProgressPayload.class);
            }

            default -> throw new IllegalArgumentException(
                    "Unsupported event type: " + event.getEventType()
            );
        }

        return event;
    }

    private static void validateType(AssetEvent<?> event, Class<?> expectedClass) {
        if (!expectedClass.isInstance(event.getData())) {
            throw new IllegalArgumentException(
                    "Expected " + expectedClass.getSimpleName() +
                            " but got " + event.getData().getClass().getSimpleName()
            );
        }
    }
}