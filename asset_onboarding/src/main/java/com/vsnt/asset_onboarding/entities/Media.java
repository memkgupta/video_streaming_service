package com.vsnt.asset_onboarding.entities;

import com.vsnt.asset_onboarding.entities.enums.MediaAccessibility;
import com.vsnt.asset_onboarding.entities.enums.MediaStatus;
import com.vsnt.asset_onboarding.entities.enums.MediaType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Media {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private MediaType mediaType;
    private String userId;
    private MediaStatus status;
    private boolean active;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private MediaAccessibility accessibility;
    @OneToOne(cascade = CascadeType.ALL , fetch = FetchType.EAGER)
    private MediaPushKey pushKey;
    @ManyToOne(fetch = FetchType.EAGER)
    private Group group;
    @OneToOne
    @JoinColumn(name = "thumbnail_asset")
    private Asset thumbnailAsset;
    @OneToOne
    @JoinColumn(name = "video_asset")
    private Asset videoAsset;

}
