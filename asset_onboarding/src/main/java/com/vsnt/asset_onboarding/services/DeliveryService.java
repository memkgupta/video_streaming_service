package com.vsnt.asset_onboarding.services;

import org.springframework.stereotype.Service;

@Service
public class DeliveryService {
    private final SegmentService segmentService;
    private final MediaService mediaService;

    public DeliveryService(SegmentService segmentService, MediaService mediaService) {
        this.segmentService = segmentService;
        this.mediaService = mediaService;
    }

}
