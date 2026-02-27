package com.vsnt.asset_onboarding.services;

import com.vsnt.asset_onboarding.dtos.kvstore.moderation.ViolationCountKV;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class ModerationKVService {
    private final RedisTemplate<String , Long>  redisTemplate;

    public ModerationKVService(RedisTemplate<String, Long> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    public long getViolationCount(String mediaId)
    {
        Long value = redisTemplate.opsForValue().get(mediaId);

        if (value == null) {
            return 0;
        }

        return value;
    }
    public long increment(String mediaId , long count)
    {
        Long nc =  redisTemplate.opsForValue().increment(mediaId, count);
        if (nc == 1) {
            redisTemplate.expire(mediaId, Duration.ofMinutes(5));
        }
        return nc;
    }
}
