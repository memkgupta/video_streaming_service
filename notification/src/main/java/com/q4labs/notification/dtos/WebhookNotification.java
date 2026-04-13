package com.q4labs.notification.dtos;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
@Builder
@Getter
public class WebhookNotification {
    private String id;           // unique event id (VERY important)
    private String eventType;    // MEDIA_PUBLISHED, LIVE_STARTED, etc
    private String source;       // media-service / live-service
    private Instant timestamp;
    private String entityId;     // mediaId / liveId / assetId
    private String userId;       // optional (who triggered)
    private Object data;         // actual payload (event specific)
    private String webhookUrl;   // where to send
}