package com.q4labs.event_service.repositories;

import com.q4labs.event_service.entities.DeliveryAttempt;
import com.q4labs.event_service.enums.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface DeliveryAttemptRepository extends JpaRepository<DeliveryAttempt, String > {
    List<DeliveryAttempt> findByStatusAndNextRetryAtBefore(DeliveryStatus status, Instant nextRetryAt);

    Optional<DeliveryAttempt> findByEventIdAndCallbackUrl(String eventId, String callbackURL);
}
