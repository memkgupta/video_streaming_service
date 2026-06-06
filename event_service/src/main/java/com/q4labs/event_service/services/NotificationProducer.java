package com.q4labs.event_service.services;
import com.q4labs.event_service.config.RabbitMQConstants;
import com.q4labs.event_service.dtos.SSENotification;
import com.q4labs.event_service.dtos.WebhookNotification;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationProducer {

    private final RabbitTemplate rabbitTemplate;

    public NotificationProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void send(WebhookNotification notification) {

        rabbitTemplate.convertAndSend(
                RabbitMQConstants.NOTIFICATIONS_EXCHANGE,
                "notification.webhooks",
                notification
        );
    }
    public void send(SSENotification notification) {

        rabbitTemplate.convertAndSend(
                RabbitMQConstants.NOTIFICATIONS_EXCHANGE,
                "notification.sse",
                notification
        );
    }
}
