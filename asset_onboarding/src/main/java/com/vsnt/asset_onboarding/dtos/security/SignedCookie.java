package com.vsnt.asset_onboarding.dtos.security;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Builder
@Data
public class SignedCookie{
    private String keyPairId;
    private String expires;
    private String signature;
}
