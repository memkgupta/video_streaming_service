package com.q4labs.notification.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.q4labs.notification.dtos.WebhookNotification;
import com.q4labs.notification.entities.DeadLetterEvent;
import com.q4labs.notification.repositories.DeadLetterEventRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class DeadLetterEventService {
    private final ObjectMapper objectMapper;
    private final DeadLetterEventRepository  deadLetterEventRepository;

    public DeadLetterEventService(ObjectMapper objectMapper, DeadLetterEventRepository deadLetterEventRepository) {
        this.objectMapper = objectMapper;
        this.deadLetterEventRepository = deadLetterEventRepository;
    }

    public void saveFailure(WebhookNotification notification, String error) {

        try {
            String payloadJson = objectMapper.writeValueAsString(notification);

            DeadLetterEvent failed = DeadLetterEvent.builder()
                    .id(notification.getId())
                    .eventType(notification.getEventType())
                    .callbackUrl(notification.getWebhookUrl())
                    .payload(payloadJson)
                    .reason(error)
                    .retryCount(0)
//                    .createdAt(Instant.now())
                    .failedAt(Instant.now())
                    .build();

            deadLetterEventRepository.save(failed);

        } catch (Exception e) {
            throw new RuntimeException("Failed to store failed notification", e);
        }
    }
}
