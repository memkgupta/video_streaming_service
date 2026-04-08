package com.q4labs.notification.dtos.requests;

import com.q4labs.notification.enums.EventType;
import lombok.Data;

@Data
public class CreateSubscriptionRequest {
    private String orgId;
    private EventType eventType;
    private String callbackUrl;
}
