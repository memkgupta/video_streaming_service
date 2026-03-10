package com.vsnt.channel_service.payload.subscription;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class SubscriptionDTO {
    private Long id;
    private String channelId;
    private String userId;
    private boolean subscribed;

   private Timestamp createdAt;
}
