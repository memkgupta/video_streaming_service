package com.vsnt.common_lib.dtos.events.media.processing;

import com.vsnt.common_lib.enums.AssetType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MediaProcessingPayload {
private String assetId;
private AssetType assetType;
private String workerId;
private Long totalChunks;
private Long expectedTime;
}
