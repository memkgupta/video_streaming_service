package com.vsnt.asset_onboarding.listeners.moderation.actions;

import com.vsnt.asset_onboarding.dtos.ModerationStatus;
import com.vsnt.asset_onboarding.dtos.ModerationUpdateDTO;

public interface ModerationAction {
    void act( ModerationUpdateDTO dto);
    ModerationStatus support();
}
