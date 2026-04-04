package com.vsnt.asset_onboarding.listeners;

import com.vsnt.asset_onboarding.dtos.TranscodingSegmentUpdateDTO;
import com.vsnt.asset_onboarding.dtos.kvstore.segments.KVSegment;
import com.vsnt.asset_onboarding.entities.Media;
import com.vsnt.asset_onboarding.entities.enums.MediaType;
import com.vsnt.asset_onboarding.services.SegmentKVService;
import org.springframework.stereotype.Component;

@Component
public class LiveSegmentUpdateHandler implements SegmentUpdateHandler{
    private final SegmentKVService segmentKVService;

    public LiveSegmentUpdateHandler(SegmentKVService segmentKVService) {
        this.segmentKVService = segmentKVService;
    }

    @Override
    public void handle(TranscodingSegmentUpdateDTO segmentUpdate) {
        segmentKVService.addSegment(
                KVSegment.builder()
                        .url(segmentUpdate.getUrl())
                        .resolution(segmentUpdate.getResolution().toResolutionString())
                        .assetId(segmentUpdate.getAssetId())
                        .duration(segmentUpdate.getDuration())
                        .sequenceNumber(segmentUpdate.getSequenceNumber())
                        .build()
        );
    }

    @Override
    public MediaType support() {
        return MediaType.LIVE;
    }
}
