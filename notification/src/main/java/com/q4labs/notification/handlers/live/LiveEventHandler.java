package com.q4labs.notification.handlers.live;

import com.vsnt.common_lib.dtos.events.live.LiveEvent;
import com.vsnt.common_lib.dtos.events.live.LiveEventType;

public interface LiveEventHandler<T>{
    LiveEventType supports();
    void handle(LiveEvent<T> event);
}
