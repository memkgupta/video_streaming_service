package com.vsnt.asset_onboarding.dtos;

import com.vsnt.asset_onboarding.entities.enums.MediaType;
import lombok.Data;

@Data
public class TranscodingFailedDTO {
    private String mediaId;
    private MediaType mediaType;
    private String message;
    private String assetId;
}
