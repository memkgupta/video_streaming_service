package com.vsnt.asset_onboarding.listeners;

import com.vsnt.asset_onboarding.entities.enums.MediaType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SegmentUpdateHandlerFactory {
    private final List<SegmentUpdateHandler> handlers;
    private final StaticSegmentUpdateHandler staticSegmentUpdateHandler;
    public SegmentUpdateHandlerFactory(List<SegmentUpdateHandler> handlers , StaticSegmentUpdateHandler staticSegmentUpdateHandler) {
        this.handlers = handlers;
        this.staticSegmentUpdateHandler = staticSegmentUpdateHandler;
    }

    public SegmentUpdateHandler getSegmentUpdateHandler(MediaType mediaType){
    return handlers.stream().filter(h -> h.support().equals(mediaType)).findFirst().orElse(
           staticSegmentUpdateHandler
    );
}
}
