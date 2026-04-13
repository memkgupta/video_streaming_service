package com.q4labs.notification.handlers.live;

import com.vsnt.common_lib.dtos.events.live.LiveEvent;
import com.vsnt.common_lib.dtos.events.live.LiveEventType;
import com.vsnt.common_lib.dtos.events.live.start.LiveStartedPayload;
import org.springframework.stereotype.Component;

@Component
public class LiveStartEventHandler implements LiveEventHandler<LiveStartedPayload> {
    @Override
    public LiveEventType supports() {
        return LiveEventType.LIVE_STARTED;
    }

    @Override
    public void handle(LiveEvent<LiveStartedPayload> event) {

    }
}
