package com.vsnt.videos_service.services;

import com.vsnt.videos_service.dtos.RedisSegmentDTO;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StreamSegmentKVService {
    private final RedisTemplate<String , RedisSegmentDTO> redisTemplate;
    private final int MAX_SEGMENTS = 10;
    public StreamSegmentKVService(RedisTemplate<String, RedisSegmentDTO> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    public void addSegment(RedisSegmentDTO segment) {
        String key = "stream:" + segment.getStreamKey() + ":segments";
        // add segment at end
        redisTemplate.opsForList().rightPush(key, segment);
        // keep only last 10
        redisTemplate.opsForList().trim(key, -MAX_SEGMENTS, -1);
    }
    public List<RedisSegmentDTO> getLatestSegments(String streamKey) {
        String key = "stream:" + streamKey + ":segments";
        return redisTemplate.opsForList().range(key, 0, -1);
    }
    public void clear(String streamKey)
    {
        String key =  "stream:" + streamKey + ":segments";
        redisTemplate.delete(key);
    }
}
