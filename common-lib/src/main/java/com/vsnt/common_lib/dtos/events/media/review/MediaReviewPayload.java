package com.vsnt.common_lib.dtos.events.media.review;

import com.vsnt.common_lib.enums.AssetType;
import lombok.Builder;

@Builder
public class MediaReviewPayload {
    private String assetId;
    private AssetType assetType;
    private Object details;
}
