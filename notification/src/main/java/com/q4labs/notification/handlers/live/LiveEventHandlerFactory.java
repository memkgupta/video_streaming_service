package com.q4labs.notification.handlers.live;

import com.vsnt.common_lib.dtos.events.live.LiveEventType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class LiveEventHandlerFactory {
    private final Map<LiveEventType, LiveEventHandler> handlerMap;
    public LiveEventHandlerFactory(List<LiveEventHandler> handlers) {
        this.handlerMap = handlers.stream()
                .collect(Collectors.toMap(
                        LiveEventHandler::supports,
                        Function.identity(),
                        (h1, h2) -> {
                            throw new IllegalStateException(
                                    "Duplicate handler for event type: " + h1.supports()
                            );
                        }
                ));
    }
    public LiveEventHandler getHandler(LiveEventType eventType) {

        LiveEventHandler handler = handlerMap.get(eventType);

        if (handler == null) {
            throw new IllegalArgumentException(
                    "No handler found for event type: " + eventType
            );
        }

        return handler;
    }
}