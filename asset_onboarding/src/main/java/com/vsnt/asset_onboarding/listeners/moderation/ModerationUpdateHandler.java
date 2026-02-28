package com.vsnt.asset_onboarding.listeners.moderation;

import com.vsnt.asset_onboarding.dtos.ModerationUpdateDTO;
import com.vsnt.asset_onboarding.entities.Media;
import com.vsnt.asset_onboarding.entities.enums.MediaType;

public interface ModerationUpdateHandler {
    public MediaType supports();
    void handle(ModerationUpdateDTO update , Media media);
}
