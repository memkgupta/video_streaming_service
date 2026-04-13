package com.q4labs.notification.entities;

import com.q4labs.notification.enums.EventType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
@Entity
@Table(name = "dead_letter_events")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeadLetterEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String eventId;
    private String eventType;
    private String callbackUrl;

    @Column(columnDefinition = "TEXT")
    private String payload;

    private String reason;
    private int retryCount;
    private Instant failedAt;
}
