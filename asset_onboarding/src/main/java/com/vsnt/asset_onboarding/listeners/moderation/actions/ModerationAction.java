package com.vsnt.asset_onboarding.listeners.moderation.actions;

import com.vsnt.asset_onboarding.dtos.ModerationStatus;
import com.vsnt.asset_onboarding.entities.Media;
import com.vsnt.asset_onboarding.entities.enums.MediaStatus;
import com.vsnt.asset_onboarding.moderation.ModerationUpdateDTO;

public interface ModerationAction {
    MediaStatus act(ModerationUpdateDTO dto , Media media);
    ModerationStatus support();
}
