package com.q4labs.notification.config;

import com.q4labs.notification.dtos.MediaStatusUpdate;
import com.q4labs.notification.dtos.Notification;
import com.q4labs.notification.handlers.MediaStatusUpdateListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumers {
    private final MediaStatusUpdateListener mediaStatusUpdateListener;

    public KafkaConsumers(MediaStatusUpdateListener mediaStatusUpdateListener) {
        this.mediaStatusUpdateListener = mediaStatusUpdateListener;
    }

    @KafkaListener(
            topics = "notifications-media-status-update",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void mediaStatusUpdate(Notification<MediaStatusUpdate> message) {
        mediaStatusUpdateListener.listen(message);
    }
}
