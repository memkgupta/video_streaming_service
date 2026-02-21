package com.vsnt.asset_onboarding.services;

import com.vsnt.asset_onboarding.dtos.segments.KVSegment;

import java.util.List;

public abstract class SegmentKVService{
    public abstract void addSegment(KVSegment segment);
    public abstract List<KVSegment> getLatestSegments(String mediaId);
    public abstract void clear(String mediaId);

}
