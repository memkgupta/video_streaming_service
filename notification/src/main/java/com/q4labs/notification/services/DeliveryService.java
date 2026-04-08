package com.q4labs.notification.services;

import com.q4labs.notification.dtos.Notification;
import com.q4labs.notification.entities.DeliveryAttempt;
import com.q4labs.notification.entities.Subscription;
import com.q4labs.notification.enums.DeliveryStatus;
import com.q4labs.notification.repositories.DeliveryAttemptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryAttemptRepository repository;
    private final RestTemplate restTemplate;

    public void deliver(Notification<?> event, Subscription sub) {

        DeliveryAttempt attempt = repository.findByEventIdAndCallbackUrl(
                event.getNotificationId(), sub.getCallbackUrl()
        ).orElse(
                DeliveryAttempt.builder()
                        .eventId(event.getNotificationId())
                        .callbackUrl(sub.getCallbackUrl())
                        .status(DeliveryStatus.PENDING)
                        .retryCount(0)
                        .createdAt(Instant.now())
                        .build()
        );

        try {
            restTemplate.postForEntity(sub.getCallbackUrl(), event, Void.class);

            attempt.setStatus(DeliveryStatus.SUCCESS);
            attempt.setNextRetryAt(null);
            attempt.setLastError(null);

        } catch (Exception e) {

            attempt.setStatus(DeliveryStatus.RETRYING);
            attempt.setRetryCount(attempt.getRetryCount() + 1);
            attempt.setLastError(e.getMessage());

            attempt.setNextRetryAt(nextRetry(attempt.getRetryCount()));
        }

        repository.save(attempt);
    }

    private Instant nextRetry(int retryCount) {
        long delay = (long) Math.pow(2, retryCount) * 10; // exponential
        return Instant.now().plusSeconds(delay);
    }
}
