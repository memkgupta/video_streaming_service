package com.vsnt.asset_onboarding.config;

import com.vsnt.asset_onboarding.dtos.kvstore.moderation.ViolationCountKV;
import com.vsnt.asset_onboarding.dtos.kvstore.segments.KVSegment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, KVSegment> redisTemplate(
            RedisConnectionFactory connectionFactory) {

        RedisTemplate<String, KVSegment> template =
                new RedisTemplate<>();

        template.setConnectionFactory(connectionFactory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        return template;
    }

    @Bean
    public RedisTemplate<String , Long> violationCountRedisTemplate(RedisConnectionFactory connectionFactory)
    {
        RedisTemplate<String, Long> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());

        template.setValueSerializer(new GenericToStringSerializer<>(Long.class));
        return template;
    }
}
