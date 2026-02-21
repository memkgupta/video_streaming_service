package com.vsnt.asset_onboarding.listeners.moderation;

import com.vsnt.asset_onboarding.entities.enums.MediaType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ModerationUpdateHandlerFactory {

private final List<ModerationUpdateHandler> handlers;

    public ModerationUpdateHandlerFactory(List<ModerationUpdateHandler> handlers) {
        this.handlers = handlers;
    }
    public ModerationUpdateHandler getModerationUpdateHandler(MediaType mediaType) {
        return handlers.stream().filter(h -> h.supports().equals(mediaType)).findFirst().orElseThrow(
                ()-> new IllegalArgumentException("Segment update handler not found")
        );
    }
}
