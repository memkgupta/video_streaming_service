package com.vsnt.asset_onboarding.dtos.media.request;

import com.vsnt.asset_onboarding.entities.enums.MediaAccessibility;
import com.vsnt.asset_onboarding.entities.enums.MediaType;
import lombok.Data;

@Data
public class MediaCreateRequestDTO {
    private MediaType mediaType;
    private MediaAccessibility  mediaAccessibility;
    private String groupId;
    private boolean moderation;
}
