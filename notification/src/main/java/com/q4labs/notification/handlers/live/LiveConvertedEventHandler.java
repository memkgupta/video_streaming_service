package com.q4labs.notification.handlers.live;

import com.vsnt.common_lib.dtos.events.live.LiveEvent;
import com.vsnt.common_lib.dtos.events.live.LiveEventType;
import com.vsnt.common_lib.dtos.events.live.converted.LiveConvertedPayload;

public class LiveConvertedEventHandler implements LiveEventHandler<LiveConvertedPayload> {
    @Override
    public LiveEventType supports() {
        return LiveEventType.LIVE_CONVERTED;
    }

    @Override
    public void handle(LiveEvent<LiveConvertedPayload> event) {

    }
}
