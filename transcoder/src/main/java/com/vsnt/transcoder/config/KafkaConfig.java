package com.vsnt.transcoder.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.internals.Topic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;

@Configuration
public class KafkaConfig {
    @Bean
    public NewTopic topic() {
        return TopicBuilder.name("video-updates").build();
    }

//    @Bean
//    public ConcurrentKafkaListenerContainerFactory<String, byte[]>
//    batchKafkaListenerContainerFactory(
//            ConsumerFactory<String, byte[]> consumerFactory) {
//
//        ConcurrentKafkaListenerContainerFactory<String, byte[]> factory =
//                new ConcurrentKafkaListenerContainerFactory<>();
//
//        factory.setConsumerFactory(consumerFactory);
//
//        factory.setBatchListener(true); // enables batching
//
//        factory.setConcurrency(4); // parallel consumers
//
//        return factory;
//    }
}
