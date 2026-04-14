package com.vsnt.common_lib.dtos.events.media.blocked;

import com.vsnt.common_lib.enums.AssetType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MediaBlockedPayload {
    private String assetId;
    private AssetType assetType;
    private Object details;
}
