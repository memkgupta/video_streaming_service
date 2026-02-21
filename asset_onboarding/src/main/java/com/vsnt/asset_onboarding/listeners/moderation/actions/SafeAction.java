package com.vsnt.asset_onboarding.listeners.moderation.actions;

import com.vsnt.asset_onboarding.dtos.ModerationStatus;
import com.vsnt.asset_onboarding.dtos.ModerationUpdateDTO;

public class SafeAction implements ModerationAction{
    @Override
    public void act(ModerationStatus status, ModerationUpdateDTO dto) {
        // eat 5 star do nothing
    }

    @Override
    public ModerationStatus support() {
        return ModerationStatus.SAFE;
    }
}
