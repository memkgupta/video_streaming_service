package com.q4labs.event_service.repositories;

import com.q4labs.event_service.entities.EventLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventLogRepository extends JpaRepository<EventLog, String> {
    EventLog findByEventId(String eventId);
}
