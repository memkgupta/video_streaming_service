package com.q4labs.notification.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.q4labs.notification.dtos.Notification;
import com.q4labs.notification.entities.EventLog;
import com.q4labs.notification.enums.EventType;
import com.q4labs.notification.repositories.EventLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class EventLogService {

    private final EventLogRepository repository;
    private final ObjectMapper objectMapper;

    public EventLog saveEvent(Notification<?> event , EventType eventType) {
        try {
            return repository.save(
                    EventLog.builder()
                            .eventId(event.getNotificationId())
                            .orgId(event.getOrgId())
                            .eventType(eventType)
                            .payload(objectMapper.writeValueAsString(event))
                            .createdAt(Instant.now())
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize event", e);
        }
    }
}
