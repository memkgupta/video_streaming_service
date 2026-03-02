package com.vsnt.asset_onboarding.dtos;

import com.vsnt.asset_onboarding.entities.enums.ResolutionEnum;
import lombok.Data;

@Data

public class TranscodingSegmentUpdateDTO {
    private String assetId;
    private String url; // url of that particular segment
    private long sequenceNumber;
    private String mediaId;
    private long duration;
    private ResolutionEnum resolution;

    @Override
    public String toString() {
        return "TranscodingSegmentUpdateDTO{" +
                "assetId='" + assetId + '\'' +
                ", url='" + url + '\'' +
                ", sequenceNumber=" + sequenceNumber +
                ", mediaId='" + mediaId + '\'' +
                ", duration=" + duration +
                ", resolution=" + resolution +
                '}';
    }
}
