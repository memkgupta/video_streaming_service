package com.q4labs.notification.entities;

import com.q4labs.notification.enums.EventType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
@Entity
@Table(name = "event_logs",
        indexes = {
                @Index(name = "idx_event_id", columnList = "eventId"),
                @Index(name = "idx_org_event", columnList = "orgId,eventType")
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String eventId;

    private String orgId;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    @Column(columnDefinition = "TEXT")
    private String payload; // JSON

    private Instant createdAt;
}
