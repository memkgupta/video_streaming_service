package com.q4labs.notification.handlers;

import com.q4labs.notification.dtos.MediaStatusUpdate;
import com.q4labs.notification.dtos.Notification;
import com.q4labs.notification.enums.EventType;
import com.q4labs.notification.services.FanoutService;
import org.springframework.stereotype.Component;

@Component
public class MediaStatusUpdateListener {
    private final FanoutService fanoutService;

    public MediaStatusUpdateListener(FanoutService fanoutService) {
        this.fanoutService = fanoutService;
    }

    public void listen(Notification<MediaStatusUpdate> mediaStatusUpdate)
    {
fanoutService.process(mediaStatusUpdate, EventType.MEDIA_STATUS_UPDATE);
    }
}
