package com.q4labs.notification.handlers.media;

import com.vsnt.common_lib.dtos.events.media.MediaEvent;
import com.vsnt.common_lib.dtos.events.media.MediaEventType;

public interface MediaEventHandler<T>{
    MediaEventType supports();
    void handle(MediaEvent<T> event);
}
