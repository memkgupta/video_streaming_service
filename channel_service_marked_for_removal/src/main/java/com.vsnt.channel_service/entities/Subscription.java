package com.vsnt.channel_service.entities;

import com.vsnt.channel_service.payload.subscription.SubscriptionDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Data
public class Subscription {
    @Id
    @GeneratedValue
    private long id;
    @ManyToOne
    private Channel channel;
    private String userId;
    private boolean notificationsOn;
    private Timestamp createdAt;
    public SubscriptionDTO toDTO()
    {
        SubscriptionDTO dto = new SubscriptionDTO();
        dto.setSubscribed(true);
        dto.setId(id);
        dto.setChannelId(channel.getId());
        dto.setUserId(userId);
        dto.setCreatedAt(createdAt);
        return dto;
    }
}
