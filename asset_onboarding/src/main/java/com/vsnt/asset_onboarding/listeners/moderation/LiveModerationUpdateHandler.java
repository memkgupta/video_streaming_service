package com.vsnt.asset_onboarding.listeners.moderation;

import com.vsnt.asset_onboarding.dtos.ModerationUpdateDTO;
import com.vsnt.asset_onboarding.entities.Media;
import com.vsnt.asset_onboarding.entities.enums.MediaType;
import com.vsnt.asset_onboarding.listeners.moderation.actions.ModerationActionFactory;
import org.springframework.stereotype.Component;

@Component
public class LiveModerationUpdateHandler implements ModerationUpdateHandler{
    private final ModerationActionFactory moderationActionFactory;

    public LiveModerationUpdateHandler(ModerationActionFactory moderationActionFactory) {
        this.moderationActionFactory = moderationActionFactory;
    }

    @Override
    public MediaType supports() {
        return MediaType.LIVE;
    }

    @Override
    public void handle(ModerationUpdateDTO update, Media media) {
        // eat 5 star do nothing
    }
}
