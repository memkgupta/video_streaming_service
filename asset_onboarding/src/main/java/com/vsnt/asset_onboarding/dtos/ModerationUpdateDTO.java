package com.vsnt.asset_onboarding.dtos;

import lombok.Data;

@Data
public class ModerationUpdateDTO {
    private ModerationStatus moderationStatus;
    private ModerationResult moderationResult;
    private String assetId;
    private String mediaId;
    private long violationCount;
}
