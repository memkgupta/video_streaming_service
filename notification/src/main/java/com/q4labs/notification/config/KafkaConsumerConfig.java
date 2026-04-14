package com.q4labs.notification.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    // ================= COMMON CONFIG =================

    private Map<String, Object> consumerConfigs(String groupId) {
        Map<String, Object> props = new HashMap<>();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, Secrets.KAFKA_BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);

        // 🔥 ONLY STRING DESERIALIZER
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        return props;
    }

    private ConcurrentKafkaListenerContainerFactory<String, String> factory(
            ConsumerFactory<String, String> consumerFactory) {

        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory);

        // manual ack
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);

        // concurrency
        factory.setConcurrency(3);

        return factory;
    }

    // ================= MEDIA =================

    @Bean
    public ConsumerFactory<String, String> mediaConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(
                consumerConfigs("notification-media-group")
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String>
    mediaKafkaListenerContainerFactory() {
        return factory(mediaConsumerFactory());
    }

    // ================= LIVE =================

    @Bean
    public ConsumerFactory<String, String> liveConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(
                consumerConfigs("notification-live-group")
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String>
    liveKafkaListenerContainerFactory() {
        return factory(liveConsumerFactory());
    }

    // ================= ASSET =================

    @Bean
    public ConsumerFactory<String, String> assetConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(
                consumerConfigs("notification-asset-group")
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String>
    assetKafkaListenerContainerFactory() {
        return factory(assetConsumerFactory());
    }
}