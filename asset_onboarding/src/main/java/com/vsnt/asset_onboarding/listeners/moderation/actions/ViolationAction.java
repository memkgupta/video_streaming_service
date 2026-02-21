package com.vsnt.asset_onboarding.listeners.moderation.actions;

import com.vsnt.asset_onboarding.dtos.ModerationStatus;
import com.vsnt.asset_onboarding.dtos.ModerationUpdateDTO;

public class ViolationAction implements ModerationAction{
    @Override
    public void act(ModerationStatus status, ModerationUpdateDTO dto) {
    //todo maintain a violation counter and also notify the org admin , once max violation reach instantly block the media
    }

    @Override
    public ModerationStatus support() {
        return ModerationStatus.VIOLATION;
    }
}
