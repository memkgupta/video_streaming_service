package com.q4labs.notification.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.q4labs.notification.handlers.asset.AssetEventHandler;
import com.q4labs.notification.handlers.asset.AssetEventHandlerFactory;
import com.q4labs.notification.handlers.live.LiveEventHandler;
import com.q4labs.notification.handlers.live.LiveEventHandlerFactory;
import com.q4labs.notification.handlers.media.MediaEventHandler;
import com.q4labs.notification.handlers.media.MediaEventHandlerFactory;
import com.q4labs.notification.validator.events.AssetEventValidator;
import com.q4labs.notification.validator.events.LiveEventValidator;
import com.q4labs.notification.validator.events.MediaEventValidator;

import com.vsnt.common_lib.dtos.events.asset.AssetEvent;
import com.vsnt.common_lib.dtos.events.live.LiveEvent;
import com.vsnt.common_lib.dtos.events.media.MediaEvent;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumers {

    private final MediaEventHandlerFactory mediaFactory;
    private final AssetEventHandlerFactory assetFactory;
    private final LiveEventHandlerFactory liveFactory;
    private final ObjectMapper objectMapper;

    public KafkaConsumers(
            MediaEventHandlerFactory mediaFactory,
            AssetEventHandlerFactory assetFactory,
            LiveEventHandlerFactory liveFactory,
            ObjectMapper objectMapper
    ) {
        this.mediaFactory = mediaFactory;
        this.assetFactory = assetFactory;
        this.liveFactory = liveFactory;
        this.objectMapper = objectMapper;
    }

    // ================= MEDIA =================

    @KafkaListener(
            topics = "media-updates",
            containerFactory = "mediaKafkaListenerContainerFactory"
    )
    public void handleMediaEvents(String message, Acknowledgment ack) {

        try {
            MediaEvent<?> event = objectMapper.readValue(
                    message,
                    new TypeReference<MediaEvent<?>>() {}
            );

            MediaEvent<?> validatedEvent = MediaEventValidator.validate(event);

            MediaEventHandler handler =
                    mediaFactory.getHandler(validatedEvent.getEventType());

            if (handler != null) {
                handler.handle(validatedEvent);
            }

            ack.acknowledge();

        } catch (Exception e) {
            System.err.println(" MEDIA EVENT FAILED: " + message);
            e.printStackTrace();

            // ⚠ prevent infinite retry loop
            ack.acknowledge();
        }
    }

    // ================= ASSET =================

    @KafkaListener(
            topics = "asset-updates",
            containerFactory = "assetKafkaListenerContainerFactory"
    )
    public void handleAssetEvents(String message, Acknowledgment ack) {

        try {
            AssetEvent<?> event = objectMapper.readValue(
                    message,
                    new TypeReference<AssetEvent<?>>() {}
            );

            AssetEvent<?> validatedEvent = AssetEventValidator.validate(event);

            AssetEventHandler handler =
                    assetFactory.getHandler(validatedEvent.getEventType());

            if (handler != null) {
                handler.handle(validatedEvent);
            }

            ack.acknowledge();

        } catch (Exception e) {
            System.err.println(" ASSET EVENT FAILED: " + message);
            e.printStackTrace();

            ack.acknowledge();
        }
    }

    // ================= LIVE =================

    @KafkaListener(
            topics = "live-updates",
            containerFactory = "liveKafkaListenerContainerFactory"
    )
    public void handleLiveEvents(String message, Acknowledgment ack) {

        try {
            LiveEvent<?> event = objectMapper.readValue(
                    message,
                    new TypeReference<LiveEvent<?>>() {}
            );

            LiveEvent<?> validatedEvent = LiveEventValidator.validate(event);

            LiveEventHandler handler =
                    liveFactory.getHandler(validatedEvent.getEventType());

            if (handler != null) {
                handler.handle(validatedEvent);
            }

            ack.acknowledge();

        } catch (Exception e) {
            System.err.println(" LIVE EVENT FAILED: " + message);
            e.printStackTrace();

            ack.acknowledge();
        }
    }
}