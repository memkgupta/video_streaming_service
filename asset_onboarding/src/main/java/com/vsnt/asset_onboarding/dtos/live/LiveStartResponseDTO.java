package com.vsnt.asset_onboarding.dtos.live;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LiveStartResponseDTO {
    private byte[] encryptionKey;
   private boolean isModerationEnabled;

}
