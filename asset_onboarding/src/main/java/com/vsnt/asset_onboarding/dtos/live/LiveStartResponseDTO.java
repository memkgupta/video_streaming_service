package com.vsnt.asset_onboarding.dtos.live;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LiveStartResponseDTO {
    private String encryptionKey;
   private boolean isModerationEnabled;
   private String assetId;

}
