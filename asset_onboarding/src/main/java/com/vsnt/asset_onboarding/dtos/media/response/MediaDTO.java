package com.vsnt.asset_onboarding.dtos.media.response;

import com.vsnt.asset_onboarding.entities.enums.MediaAccessibility;
import com.vsnt.asset_onboarding.entities.enums.MediaStatus;
import com.vsnt.asset_onboarding.entities.enums.MediaType;
import lombok.Builder;
import lombok.Data;


import java.sql.Timestamp;
import java.util.UUID;
@Data
@Builder
public class MediaDTO {
    private UUID id;
    private boolean active;
    private MediaType mediaType;
    private String userId;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private MediaAccessibility accessibility;
    private GroupDTO group;
    private MediaStatus status;
    private String pushKey;
}
