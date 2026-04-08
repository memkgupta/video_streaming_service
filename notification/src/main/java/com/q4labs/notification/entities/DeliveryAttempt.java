package com.q4labs.notification.entities;

import com.q4labs.notification.enums.DeliveryStatus;
import com.q4labs.notification.enums.EventType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
@Entity
@Table(name = "delivery_attempts",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"eventId", "callbackUrl"})
        },
        indexes = {
                @Index(name = "idx_status_retry", columnList = "status,nextRetryAt")
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String eventId;

    private String callbackUrl;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;

    private int retryCount;

    private Instant nextRetryAt;

    private String lastError;

    private Instant createdAt;
}
