package com.vsnt.asset_onboarding.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
public class MediaPushKey {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(unique = true)
    private String key;
    private Timestamp createdAt;
    private boolean active;

    private Timestamp updatedAt;
    @PreUpdate
    public void preUpdate() {
        updatedAt = Timestamp.from(Instant.now());
    }
    @PrePersist
    public void prePersist() {
        createdAt = Timestamp.from(Instant.now());
        updatedAt = Timestamp.from(Instant.now());
    }
}
