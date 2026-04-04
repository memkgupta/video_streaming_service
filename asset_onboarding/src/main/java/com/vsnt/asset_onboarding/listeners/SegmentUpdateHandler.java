package com.vsnt.asset_onboarding.listeners;

import com.vsnt.asset_onboarding.dtos.TranscodingSegmentUpdateDTO;
import com.vsnt.asset_onboarding.entities.Media;
import com.vsnt.asset_onboarding.entities.enums.MediaType;

public interface SegmentUpdateHandler {
    public void handle(TranscodingSegmentUpdateDTO segmentUpdate);
    public MediaType support();
}
