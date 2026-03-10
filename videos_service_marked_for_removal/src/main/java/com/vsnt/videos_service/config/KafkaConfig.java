package com.vsnt.videos_service.config;

import com.vsnt.videos_service.dtos.StreamSegmentUpdateDTO;
import com.vsnt.videos_service.dtos.UpdateRequestDTO;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    private Map<String, Object> baseProps() {
        Map<String, Object> props = new HashMap<>();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return props;
    }

    // Factory for UpdateRequestDTO
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UpdateRequestDTO>
    updateRequestKafkaListenerFactory() {

        JsonDeserializer<UpdateRequestDTO> deserializer =
                new JsonDeserializer<>(UpdateRequestDTO.class);

        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(false);

        DefaultKafkaConsumerFactory<String, UpdateRequestDTO> factory =
                new DefaultKafkaConsumerFactory<>(
                        baseProps(),
                        new StringDeserializer(),
                        deserializer
                );

        ConcurrentKafkaListenerContainerFactory<String, UpdateRequestDTO> container =
                new ConcurrentKafkaListenerContainerFactory<>();

        container.setConsumerFactory(factory);

        return container;
    }

    // Factory for StreamSegmentUpdateDTO
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, StreamSegmentUpdateDTO>
    streamSegmentKafkaListenerFactory() {

        JsonDeserializer<StreamSegmentUpdateDTO> deserializer =
                new JsonDeserializer<>(StreamSegmentUpdateDTO.class);

        deserializer.addTrustedPackages("*");

        DefaultKafkaConsumerFactory<String, StreamSegmentUpdateDTO> factory =
                new DefaultKafkaConsumerFactory<>(
                        baseProps(),
                        new StringDeserializer(),
                        deserializer
                );

        ConcurrentKafkaListenerContainerFactory<String, StreamSegmentUpdateDTO> container =
                new ConcurrentKafkaListenerContainerFactory<>();

        container.setConsumerFactory(factory);

        return container;
    }
}
