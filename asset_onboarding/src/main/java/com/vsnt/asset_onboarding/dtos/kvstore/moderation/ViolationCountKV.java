package com.vsnt.asset_onboarding.dtos.kvstore.moderation;

import lombok.Data;

@Data
public class ViolationCountKV {
    private String mediaId;
    private long count;
}
