package com.vsnt.asset_onboarding.dtos.kvstore.segments;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KVSegment {
    private String assetId;
    private long sequenceNumber;
    private String url;
    private long duration;
    private String resolution;
}
