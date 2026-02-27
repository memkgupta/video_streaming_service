package com.vsnt.asset_onboarding.dtos;

import lombok.Data;

@Data
public class TranscodingSegmentUpdateDTO {
    private String assetId;
    private String url; // url of that particular segment
    private long sequenceNumber;
    private String mediaId;
    private long duration;
}
