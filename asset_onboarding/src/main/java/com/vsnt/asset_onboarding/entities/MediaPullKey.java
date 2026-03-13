package com.vsnt.asset_onboarding.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.UUID;
@Entity
@Getter
@Setter
public class MediaPullKey {
@Id

@GeneratedValue(strategy = GenerationType.UUID)
private UUID id;
private String userId;
@Column(unique = true)
private String key;
private Timestamp createdAt;
private boolean active;
private String mediaId;
}
