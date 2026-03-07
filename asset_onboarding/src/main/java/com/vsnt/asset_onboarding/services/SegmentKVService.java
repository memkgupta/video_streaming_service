package com.vsnt.asset_onboarding.services;

import com.vsnt.asset_onboarding.dtos.kvstore.segments.KVSegment;

import java.util.List;
import java.util.Map;

public abstract class SegmentKVService{
    public abstract void addSegment(KVSegment segment);
    public abstract List<KVSegment> getLatestSegments(String mediaId,String resolutions);
    public abstract void clear(String mediaId);

}
