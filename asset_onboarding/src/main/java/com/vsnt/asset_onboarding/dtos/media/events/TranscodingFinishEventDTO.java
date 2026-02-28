package com.vsnt.asset_onboarding.dtos.media.events;

import com.vsnt.asset_onboarding.entities.enums.MediaType;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class TranscodingFinishEventDTO {
    private String mediaId;
    private MediaType mediaType;
    private Timestamp finishedAt;

}
