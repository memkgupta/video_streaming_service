package com.vsnt.asset_onboarding.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Getter
@Setter
public class MediaAccessToken {
@Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String refreshToken;
    private Timestamp createdAt;
    private Long validity;

private String userId;
private String assetId;
}
