package com.vsnt.asset_onboarding.services;

import com.vsnt.asset_onboarding.dtos.segments.KVSegment;
import com.vsnt.asset_onboarding.repositories.TranscodedSegmentRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RedisSegmentKVService extends SegmentKVService{
    private final int MAX_SEGMENTS = 10;
    private final RedisTemplate<String, KVSegment> redisTemplate;
    public RedisSegmentKVService(RedisTemplate<String, KVSegment> redisTemplate) {

        this.redisTemplate = redisTemplate;
    }

    public void addSegment(KVSegment segment) {
        String key = "stream:" + segment.getAssetId() + ":segments";
        // add segment at end
        redisTemplate.opsForList().rightPush(key, segment);
        // keep only last 10
        redisTemplate.opsForList().trim(key, -MAX_SEGMENTS, -1);
    }
    public List<KVSegment> getLatestSegments(String streamKey) {
        String key = "stream:" + streamKey + ":segments";
        return redisTemplate.opsForList().range(key, 0, -1);
    }
    public void clear(String streamKey)
    {
        String key =  "stream:" + streamKey + ":segments";
        redisTemplate.delete(key);
    }
}
