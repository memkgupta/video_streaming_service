package com.vsnt.user.dtos.requests;

import lombok.Data;

@Data
public class CreateWebhookRequest {
    private String orgId;
    private String eventType;
    private String secret;
    private String callbackUrl;
}
