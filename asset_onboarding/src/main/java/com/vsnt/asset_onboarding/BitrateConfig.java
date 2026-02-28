package com.vsnt.asset_onboarding;

import com.vsnt.asset_onboarding.entities.enums.ResolutionEnum;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class BitrateConfig {
    private final long bandwidth;                // required
    private final Long averageBandwidth;         // optional
    private final ResolutionEnum resolution;         // optional
    private final String codecs;                 // optional
    private final Double frameRate;              // optional
    private final String audioGroup;             // optional
    private final String subtitlesGroup;         // optional
    private final String closedCaptions;
}
