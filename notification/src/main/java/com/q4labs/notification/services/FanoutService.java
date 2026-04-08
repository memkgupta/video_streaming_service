package com.q4labs.notification.services;

import com.q4labs.notification.dtos.Notification;
import com.q4labs.notification.entities.Subscription;
import com.q4labs.notification.enums.EventType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class FanoutService {
    private final SubscriptionService subscriptionService;
    private final DeliveryService deliveryService;
    private final EventLogService eventLogService;
    public void process(Notification<?> event, EventType eventType) {
        // 1. Store event
        eventLogService.saveEvent(event,eventType);
        // 2. Get subscribers
        List<Subscription> subs =
                subscriptionService.getActiveSubscriptions(
                        event.getOrgId(),
                      eventType
                );
        // 3. Fanout
        subs.forEach(sub ->
                CompletableFuture.runAsync(() -> deliveryService.deliver(event, sub))
        );
    }
}
