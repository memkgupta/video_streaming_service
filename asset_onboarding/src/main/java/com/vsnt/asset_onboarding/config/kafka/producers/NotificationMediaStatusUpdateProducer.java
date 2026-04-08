package com.vsnt.asset_onboarding.config.kafka.producers;

import com.vsnt.asset_onboarding.dtos.media.notification.MediaStatusUpdate;
import com.vsnt.asset_onboarding.dtos.notification.Notification;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class NotificationMediaStatusUpdateProducer extends KafkaProducer<Notification<MediaStatusUpdate>> {
    public NotificationMediaStatusUpdateProducer(KafkaTemplate<String, Notification<MediaStatusUpdate>> kafkaTemplate) {
        super(kafkaTemplate, "notifications-media-status-update");
    }

    @Override
    public void produceMessage(Notification<MediaStatusUpdate> message) {
    kafkaTemplate.send(topic, message);
    }
}
