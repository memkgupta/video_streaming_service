package com.q4labs.event_service.workers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.q4labs.event_service.config.RabbitMQConstants;
import com.q4labs.event_service.dtos.WebhookNotification;
import com.q4labs.event_service.services.DeadLetterEventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class WebhookDLQWorker {

    private final DeadLetterEventService deadLetterEventService;
    private final ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(WebhookDLQWorker.class);
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
            logger.info("Storing notification to DLQ {}", notification.getId());
            deadLetterEventService.saveFailure(
                    notification,
                    "Moved to DLQ after retries"
            );

        } catch (Exception e) {
           logger.error("Error while processing DLQ notification", e);
        }
    }
}