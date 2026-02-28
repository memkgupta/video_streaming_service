package com.vsnt.asset_onboarding.listeners.media;

import com.vsnt.asset_onboarding.entities.Media;
import com.vsnt.asset_onboarding.entities.enums.MediaType;

public interface MediaFinishHandler {
    public void handle(Media media);
    MediaType supports();

}
