package com.vsnt.asset_onboarding.listeners.moderation.actions;

import com.vsnt.asset_onboarding.dtos.ModerationStatus;
import com.vsnt.asset_onboarding.moderation.ModerationUpdateDTO;
import org.springframework.stereotype.Component;

@Component
public class ReviewAction implements ModerationAction{
    @Override
    public void act(ModerationUpdateDTO dto) {
        //todo mark the media for review and notify the org to which media belongs to
    }

    @Override
    public ModerationStatus support() {
        return ModerationStatus.FLAGGED;
    }
}
