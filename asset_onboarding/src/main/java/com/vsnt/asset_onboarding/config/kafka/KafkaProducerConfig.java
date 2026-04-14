package com.vsnt.asset_onboarding.config.kafka;

import com.fasterxml.jackson.databind.JsonDeserializer;

import com.vsnt.asset_onboarding.config.Secrets;
import com.vsnt.asset_onboarding.dtos.media.notification.BlockMedia;
import com.vsnt.asset_onboarding.dtos.media.notification.MediaStatusUpdate;
import com.vsnt.asset_onboarding.dtos.notification.Notification;
import com.vsnt.common_lib.dtos.events.live.LiveEvent;
import com.vsnt.common_lib.dtos.events.media.MediaEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {
    private Map<String, Object> baseConfig() {
        Map<String, Object> config = new HashMap<>();

        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, Secrets.KAFKA_BOOTSTRAP_SERVERS);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        config.put(ProducerConfig.ACKS_CONFIG, "all");
        config.put(ProducerConfig.RETRIES_CONFIG, 3);

        // Important for JSON
        config.put("spring.json.add.type.headers", false);

        return config;
    }

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> config = new HashMap<>();

        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, Secrets.KAFKA_BOOTSTRAP_SERVERS);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        config.put(ProducerConfig.ACKS_CONFIG, "all");
        config.put(ProducerConfig.RETRIES_CONFIG, 3);

        return new DefaultKafkaProducerFactory<>(config);
    }
    @Bean
    public ProducerFactory<String, MediaEvent> mediaEventProducerFactory() {
        return new DefaultKafkaProducerFactory<>(baseConfig());
    }
    @Bean
    public ProducerFactory<String, LiveEvent> liveEventProducerFactory() {
        return new DefaultKafkaProducerFactory<>(baseConfig());
    }
//    @Bean
//    public ProducerFactory<String, MediaEvent> mediaEventProducerFactory() {
//        return new DefaultKafkaProducerFactory<>(baseConfig());
//    }
    @Bean
    public KafkaTemplate<String, MediaEvent> mediaEventKafkaTemplate() {
        return new KafkaTemplate<>(mediaEventProducerFactory());
    }
    @Bean
    public KafkaTemplate<String, LiveEvent> liveEventKafkaTemplate() {
        return new KafkaTemplate<>(liveEventProducerFactory());
    }
}
