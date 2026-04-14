package com.vsnt.asset_onboarding.listeners.media;

import com.vsnt.asset_onboarding.MessageListener;
import com.vsnt.asset_onboarding.config.kafka.producers.MediaUpdateProducer;
import com.vsnt.asset_onboarding.dtos.media.events.TranscodingFinishEventDTO;
import com.vsnt.asset_onboarding.entities.Media;
import com.vsnt.asset_onboarding.services.MediaService;
import com.vsnt.common_lib.dtos.events.media.publish.MediaPublishPayload;
import com.vsnt.common_lib.dtos.events.media.publish.MediaPublishedEvent;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class MediaTranscodingFinishListener implements MessageListener<TranscodingFinishEventDTO> {
    private final MediaService mediaService;
    private final MediaUpdateProducer mediaUpdateProducer;
    private final MediaTranscodingFinishHandlerFactory factory;
    public MediaTranscodingFinishListener(MediaService mediaService, MediaUpdateProducer mediaUpdateProducer, MediaTranscodingFinishHandlerFactory factory) {
        this.mediaService = mediaService;
        this.mediaUpdateProducer = mediaUpdateProducer;
        this.factory = factory;
    }

    @Override
    public void onMessage(TranscodingFinishEventDTO message) {
        Media media = mediaService.getMedia(UUID.fromString(message.getMediaId()));
    factory.getMediaFinishHandler(media.getMediaType()).handle(media);
    mediaUpdateProducer.produceMessage(
            new MediaPublishedEvent(
                    media.getId().toString(),
                    Instant.now(),
                    media.getOrgId(),
                    MediaPublishPayload.builder()

                            .build()
            )
    );
    }
}
