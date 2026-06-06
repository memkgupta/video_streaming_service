package com.vsnt.user.dtos.responses;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class WebhookResponse {
    private String id;
    private String orgId;
    private String eventType;
    private String callbackUrl;
    private boolean active;
    private Instant createdAt;
    private String secret;
}
