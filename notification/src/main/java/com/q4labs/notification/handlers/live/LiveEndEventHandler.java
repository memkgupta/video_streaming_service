package com.q4labs.notification.handlers.live;

import com.vsnt.common_lib.dtos.events.live.LiveEvent;
import com.vsnt.common_lib.dtos.events.live.LiveEventType;
import com.vsnt.common_lib.dtos.events.live.end.LiveEndPayload;
import org.springframework.stereotype.Component;

@Component
public class LiveEndEventHandler implements LiveEventHandler<LiveEndPayload> {
    @Override
    public LiveEventType supports() {
        return LiveEventType.LIVE_ENDED;
    }

    @Override
    public void handle(LiveEvent<LiveEndPayload> event) {
        System.out.println("Live end "+event.getLiveAssetId());
    }
}
