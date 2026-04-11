package com.vsnt.asset_onboarding.listeners.moderation.actions;

import com.vsnt.asset_onboarding.dtos.ModerationStatus;
import com.vsnt.asset_onboarding.entities.Media;
import com.vsnt.asset_onboarding.entities.enums.MediaStatus;
import com.vsnt.asset_onboarding.moderation.ModerationUpdateDTO;
import org.springframework.stereotype.Component;

@Component
public class ReviewAction implements ModerationAction{
    @Override
    public MediaStatus act(ModerationUpdateDTO dto, Media media) {
        //todo mark the media for review and notify the org to which media belongs to
        return media.getStatus();
    }

    @Override
    public ModerationStatus support() {
        return ModerationStatus.FLAGGED;
    }
}
