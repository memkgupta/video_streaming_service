package com.q4labs.event_service.dtos.responses;

import com.q4labs.event_service.enums.EventType;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class SubscriptionResponse {
    private String id;
    private String orgId;
    private EventType eventType;
    private String callbackUrl;
    private boolean active;
    private Instant createdAt;
    private String secret;
}
