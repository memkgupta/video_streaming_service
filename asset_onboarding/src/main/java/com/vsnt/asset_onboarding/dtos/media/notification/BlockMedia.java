package com.vsnt.asset_onboarding.dtos.media.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BlockMedia {
    private String mediaId;
    private Instant timestamp;
}
