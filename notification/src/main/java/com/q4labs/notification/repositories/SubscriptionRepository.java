package com.q4labs.notification.repositories;

import com.q4labs.notification.entities.Subscription;
import com.q4labs.notification.enums.EventType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription,String> {

    List<Subscription> findByOrgIdAndEventTypeAndActiveTrue(String orgId, EventType eventType);

    Subscription findByCallbackUrl(String callbackUrl);
}
