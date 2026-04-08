package com.q4labs.notification.entities;

import com.q4labs.notification.enums.EventType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "subscriptions",
        indexes = {
                @Index(name = "idx_org_event", columnList = "orgId,eventType")
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String orgId;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    private String callbackUrl;

    private String secret; // for webhook signature

    private boolean active;

    private Instant createdAt;
}