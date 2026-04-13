package com.q4labs.notification.handlers.asset;

import com.vsnt.common_lib.dtos.events.asset.AssetEvent;
import com.vsnt.common_lib.dtos.events.asset.AssetEventType;

public interface AssetEventHandler<T> {
    AssetEventType supports();
    void handle(AssetEvent<T> event);
}
