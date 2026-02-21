package com.vsnt.asset_onboarding.listeners;

import com.vsnt.asset_onboarding.entities.enums.MediaType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SegmentUpdateHandlerFactory {
    private final List<SegmentUpdateHandler> handlers;

    public SegmentUpdateHandlerFactory(List<SegmentUpdateHandler> handlers) {
        this.handlers = handlers;
    }

    public SegmentUpdateHandler getSegmentUpdateHandler(MediaType mediaType){
    return handlers.stream().filter(h -> h.support().equals(mediaType)).findFirst().orElseThrow(
            ()-> new IllegalArgumentException("Segment update handler not found")
    );
}
}
