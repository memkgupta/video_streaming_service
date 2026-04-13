package com.q4labs.notification.config;


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
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumers {

    private final MediaEventHandlerFactory mediaFactory;
    private final AssetEventHandlerFactory assetFactory;
    private final LiveEventHandlerFactory liveFactory;

    public KafkaConsumers(
            MediaEventHandlerFactory mediaFactory,
            AssetEventHandlerFactory assetFactory,
            LiveEventHandlerFactory liveFactory
    ) {
        this.mediaFactory = mediaFactory;
        this.assetFactory = assetFactory;
        this.liveFactory = liveFactory;
    }


    @KafkaListener(
            topics = "media-updates",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleMediaEvents(MediaEvent<?> event) {


        MediaEvent<?> validatedEvent = MediaEventValidator.validate(event);


        MediaEventHandler handler =
                mediaFactory.getHandler(validatedEvent.getEventType());


        handler.handle(validatedEvent);
    }


    @KafkaListener(
            topics = "asset-updates",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleAssetEvents(AssetEvent<?> event) {

        AssetEvent<?> validatedEvent = AssetEventValidator.validate(event);

        AssetEventHandler handler =
                assetFactory.getHandler(validatedEvent.getEventType());

        handler.handle(validatedEvent);
    }

    @KafkaListener(
            topics = "live-updates",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleLiveEvents(LiveEvent<?> event) {

        LiveEvent<?> validatedEvent = LiveEventValidator.validate(event);

        LiveEventHandler handler =
                liveFactory.getHandler(validatedEvent.getEventType());

        handler.handle(validatedEvent);
    }
}