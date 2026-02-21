package com.vsnt.asset_onboarding.dtos.segments;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KVSegment {
    private String assetId;
    private long sequenceNumber;
    private String url;
    private long duration;
}
