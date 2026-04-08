package com.q4labs.notification.repositories;

import com.q4labs.notification.entities.DeadLetterEvent;
import com.q4labs.notification.entities.EventLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventLogRepository extends JpaRepository<EventLog, String> {
    EventLog findByEventId(String eventId);
}
