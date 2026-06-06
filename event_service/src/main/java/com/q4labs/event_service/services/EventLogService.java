package com.q4labs.event_service.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.q4labs.event_service.entities.EventLog;
import com.q4labs.event_service.enums.EventType;
import com.q4labs.event_service.repositories.EventLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class EventLogService {

    private final EventLogRepository repository;
    private final ObjectMapper objectMapper;

    public EventLog saveEvent(Object event, String orgId,String eventType) {
        try {
            return repository.save(
                    EventLog.builder()
                            .orgId(orgId)
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
