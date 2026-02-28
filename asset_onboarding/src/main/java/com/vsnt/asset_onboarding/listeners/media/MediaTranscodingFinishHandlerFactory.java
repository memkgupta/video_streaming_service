package com.vsnt.asset_onboarding.listeners.media;

import com.vsnt.asset_onboarding.entities.enums.MediaType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MediaTranscodingFinishHandlerFactory {
    private final List<MediaFinishHandler> mediaFinishHandlers;

    public MediaTranscodingFinishHandlerFactory(List<MediaFinishHandler> mediaFinishHandlers) {
        this.mediaFinishHandlers = mediaFinishHandlers;
    }
    public MediaFinishHandler getMediaFinishHandler(MediaType mediaType) {
        return mediaFinishHandlers.stream().filter(
                mh->mh.supports().equals(mediaType)
        ).findFirst().orElseThrow();
    }
}
