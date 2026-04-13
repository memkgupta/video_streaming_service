package com.q4labs.notification.handlers.media;

import com.vsnt.common_lib.dtos.events.media.MediaEventType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class MediaEventHandlerFactory {

    private final Map<MediaEventType, MediaEventHandler> handlerMap;

    public MediaEventHandlerFactory(List<MediaEventHandler> handlers) {
        this.handlerMap = handlers.stream()
                .collect(Collectors.toMap(
                        MediaEventHandler::supports,
                        Function.identity(),
                        (h1, h2) -> {
                            throw new IllegalStateException(
                                    "Duplicate handler for event type: " + h1.supports()
                            );
                        }
                ));
    }

    public MediaEventHandler getHandler(MediaEventType eventType) {

        MediaEventHandler handler = handlerMap.get(eventType);

        if (handler == null) {
            throw new IllegalArgumentException(
                    "No handler found for event type: " + eventType
            );
        }

        return handler;
    }
}