package com.vsnt.asset_onboarding.listeners.moderation.actions;

import com.vsnt.asset_onboarding.dtos.ModerationStatus;
import com.vsnt.asset_onboarding.moderation.ModerationUpdateDTO;

public class SafeAction implements ModerationAction{
    @Override
    public void act( ModerationUpdateDTO dto) {
        // eat 5 star do nothing
    }

    @Override
    public ModerationStatus support() {
        return ModerationStatus.APPROVED;
    }
}
