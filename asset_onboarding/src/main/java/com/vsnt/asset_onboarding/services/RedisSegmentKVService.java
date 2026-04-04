package com.vsnt.asset_onboarding.services;

import com.vsnt.asset_onboarding.dtos.kvstore.segments.KVSegment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RedisSegmentKVService extends SegmentKVService{
    private final int MAX_SEGMENTS = 10;
    private final RedisTemplate<String, KVSegment> redisTemplate;
    public RedisSegmentKVService(RedisTemplate<String, KVSegment> redisTemplate) {

        this.redisTemplate = redisTemplate;
    }

    public void addSegment(KVSegment segment) {
        String key = "stream:" + segment.getAssetId() + ":segments:"+segment.getResolution();
        // add segment at end
        redisTemplate.opsForList().rightPush(key, segment);
        // keep only last 10
        redisTemplate.opsForList().trim(key, -MAX_SEGMENTS, -1);
    }
    public List<KVSegment> getLatestSegments(String streamKey, String resolution) {
       Map<String,List<KVSegment>> result = new HashMap<>();


        return  getSegments("stream:"+streamKey+":segments:"+resolution);
    }
    private List<KVSegment> getSegments(String key){
      List<KVSegment> result = redisTemplate.opsForList().range(key, 0, -1);
        if(result == null){
            result = new ArrayList<>();
        }
        result.sort((s1, s2) -> Math.toIntExact(s1.getSequenceNumber() - s2.getSequenceNumber()));
        return result;
    }
    public void clear(String streamKey)
    {
        String key =  "stream:" + streamKey + ":segments";
        redisTemplate.delete(key);
    }
}
