package com.q4labs.notification.workers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.q4labs.notification.config.RabbitMQConstants;
import com.q4labs.notification.dtos.WebhookNotification;
import com.q4labs.notification.entities.DeadLetterEvent;
import com.q4labs.notification.repositories.DeadLetterEventRepository;
import com.q4labs.notification.services.DeadLetterEventService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class WebhookDLQWorker {

    private final DeadLetterEventService deadLetterEventService;
    private final ObjectMapper objectMapper;

    public WebhookDLQWorker(
            DeadLetterEventService deadLetterEventService,
            ObjectMapper objectMapper
    ) {
        this.deadLetterEventService = deadLetterEventService;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(
            queues = RabbitMQConstants.WEBHOOK_DLQ,
            containerFactory = "rabbitListenerContainerFactory"
    )
    public void consume(Object message) {

        try {

            WebhookNotification notification =
                    objectMapper.convertValue(message, WebhookNotification.class);

            deadLetterEventService.saveFailure(
                    notification,
                    "Moved to DLQ after retries"
            );

        } catch (Exception e) {
            System.err.println(" Failed to process DLQ message: " + message);
        }
    }
}