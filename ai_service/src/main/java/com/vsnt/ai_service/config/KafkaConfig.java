package com.vsnt.ai_service.config;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.vsnt.common_lib.dtos.events.transcription.TranscriptEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;


@Configuration
@EnableKafka
public class KafkaConfig {
    private final String bootstrapServers = System.getenv("KAFKA_BOOTSTRAP_SERVERS");
    private String groupId = "ai-service";
@Bean
public Map<String, Object> consumerConfigs() {
    Map<String, Object> props = new HashMap<>();

    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,  bootstrapServers);
    props.put(ConsumerConfig.GROUP_ID_CONFIG,           groupId);
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,   StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);

    // Offset
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,    "earliest");
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,   false);

    // Throughput
    props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG,     500);
    props.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG,      1);
    props.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG,    500);

    return props;
}
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TranscriptEvent>
    transcriptEventContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, TranscriptEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(
                transcriptConsumerFactory()
        );
        return factory;
    }
    @Bean
    public ConsumerFactory<String, TranscriptEvent> transcriptConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(
                consumerConfigs(),
                new StringDeserializer(),
                new JacksonJsonDeserializer<>(TranscriptEvent.class, false)
        );
    }

}
