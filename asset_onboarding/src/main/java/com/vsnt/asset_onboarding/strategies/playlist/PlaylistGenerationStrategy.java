package com.vsnt.asset_onboarding.strategies.playlist;

import com.vsnt.asset_onboarding.BitrateConfig;
import com.vsnt.asset_onboarding.entities.Media;

public interface PlaylistGenerationStrategy {
    public String generate(Media media , BitrateConfig bitrateConfig);

}
