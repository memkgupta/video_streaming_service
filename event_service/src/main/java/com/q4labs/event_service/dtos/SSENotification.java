package com.q4labs.event_service.dtos;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class SSENotification {
    private String event_id;
    private Object data;
    private String eventType;    // MEDIA_PUBLISHED, LIVE_STARTED, etc
    private String source;       // media-service / live-service
    private Instant timestamp;
    private String entityId; // media_id
}
