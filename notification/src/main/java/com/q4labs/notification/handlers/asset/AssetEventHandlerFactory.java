package com.q4labs.notification.handlers.asset;

import com.vsnt.common_lib.dtos.events.asset.AssetEventType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class AssetEventHandlerFactory {

    private final Map<AssetEventType, AssetEventHandler> handlerMap;

    public AssetEventHandlerFactory(List<AssetEventHandler> handlers) {
        this.handlerMap = handlers.stream()
                .collect(Collectors.toMap(
                        AssetEventHandler::supports,
                        Function.identity(),
                        (h1, h2) -> {
                            throw new IllegalStateException(
                                    "Duplicate handler for event type: " + h1.supports()
                            );
                        }
                ));
    }

    public AssetEventHandler getHandler(AssetEventType eventType) {

        AssetEventHandler handler = handlerMap.get(eventType);

        if (handler == null) {
            throw new IllegalArgumentException(
                    "No handler found for event type: " + eventType
            );
        }

        return handler;
    }
}