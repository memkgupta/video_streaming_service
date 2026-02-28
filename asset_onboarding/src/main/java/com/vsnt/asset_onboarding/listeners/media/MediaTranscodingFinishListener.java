package com.vsnt.asset_onboarding.listeners.media;

import com.vsnt.asset_onboarding.MessageListener;
import com.vsnt.asset_onboarding.dtos.media.events.TranscodingFinishEventDTO;
import com.vsnt.asset_onboarding.entities.Media;
import com.vsnt.asset_onboarding.services.MediaService;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class MediaTranscodingFinishListener implements MessageListener<TranscodingFinishEventDTO> {
    private final MediaService mediaService;
    private final MediaTranscodingFinishHandlerFactory factory;
    public MediaTranscodingFinishListener(MediaService mediaService, MediaTranscodingFinishHandlerFactory factory) {
        this.mediaService = mediaService;
        this.factory = factory;
    }

    @Override
    public void onMessage(TranscodingFinishEventDTO message) {
        Media media = mediaService.getMedia(UUID.fromString(message.getMediaId()));
    factory.getMediaFinishHandler(media.getMediaType()).handle(media);
    }
}
