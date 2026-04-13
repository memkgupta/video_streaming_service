package com.vsnt.common_lib.dtos.events.media.publish;

import com.vsnt.common_lib.enums.AssetType;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@Builder
public class MediaPublishPayload {
private String assetId;
private AssetType assetType;
}
