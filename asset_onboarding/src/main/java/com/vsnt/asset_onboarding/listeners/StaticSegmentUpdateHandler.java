package com.vsnt.asset_onboarding.listeners;

import com.vsnt.asset_onboarding.dtos.TranscodingSegmentUpdateDTO;
import com.vsnt.asset_onboarding.entities.Media;
import com.vsnt.asset_onboarding.entities.enums.MediaType;
import org.springframework.stereotype.Component;

@Component
public class StaticSegmentUpdateHandler implements SegmentUpdateHandler{
    @Override
    public void handle(TranscodingSegmentUpdateDTO segmentUpdate, Media media) {
        // eat 5 star do nothing
    }

    @Override
    public MediaType support() {
        return MediaType.STATIC;
    }
}
