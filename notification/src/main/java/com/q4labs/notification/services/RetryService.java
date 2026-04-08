package com.q4labs.notification.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.q4labs.notification.dtos.Notification;
import com.q4labs.notification.entities.DeliveryAttempt;
import com.q4labs.notification.entities.EventLog;
import com.q4labs.notification.entities.Subscription;
import com.q4labs.notification.enums.DeliveryStatus;
import com.q4labs.notification.repositories.DeliveryAttemptRepository;
import com.q4labs.notification.repositories.EventLogRepository;
import com.q4labs.notification.repositories.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RetryService {

    private final DeliveryAttemptRepository repository;
    private final DeliveryService deliveryService;
    private final SubscriptionRepository subscriptionRepository;
    private final EventLogRepository eventLogRepository;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 5000)
    public void retryFailed() {

        List<DeliveryAttempt> attempts =
                repository.findByStatusAndNextRetryAtBefore(
                        DeliveryStatus.RETRYING, Instant.now()
                );

        for (DeliveryAttempt attempt : attempts) {
            try {
                EventLog eventLog =
                        eventLogRepository.findByEventId(attempt.getEventId());

                Notification<?> event =
                        objectMapper.readValue(eventLog.getPayload(), Notification.class);

                Subscription sub =
                        subscriptionRepository.findByCallbackUrl(attempt.getCallbackUrl());

                deliveryService.deliver(event, sub);

            } catch (Exception e) {
                // log and continue
            }
        }
    }
}
