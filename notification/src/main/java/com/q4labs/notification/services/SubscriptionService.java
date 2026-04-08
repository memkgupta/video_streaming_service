package com.q4labs.notification.services;

import com.q4labs.notification.entities.Subscription;
import com.q4labs.notification.enums.EventType;
import com.q4labs.notification.repositories.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository repository;

    public List<Subscription> getActiveSubscriptions(String orgId, EventType eventType) {
        return repository.findByOrgIdAndEventTypeAndActiveTrue(orgId, eventType);
    }

    public Subscription create(Subscription sub) {
        sub.setCreatedAt(Instant.now());
        sub.setActive(true);
        return repository.save(sub);
    }
}
