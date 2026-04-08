package com.vsnt.asset_onboarding.config.kafka;

import com.vsnt.asset_onboarding.config.Secrets;
import com.vsnt.asset_onboarding.moderation.ModerationUpdateDTO;
import com.vsnt.asset_onboarding.dtos.TranscodingSegmentUpdateDTO;
import com.vsnt.asset_onboarding.dtos.media.events.TranscodingFinishEventDTO;
import com.vsnt.asset_onboarding.dtos.media.request.GroupCreateRequestDTO;
import com.vsnt.asset_onboarding.dtos.media.request.GroupMemberCreateRequestDTO;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {
//    @Bean
//    public DefaultErrorHandler errorHandler(KafkaTemplate<String, Object> kafkaTemplate) {
//
//        // Retry 3 times with 2s gap
//        FixedBackOff backOff = new FixedBackOff(2000L, 3);
//
//        // Dead Letter Topic handler
//        DeadLetterPublishingRecoverer recoverer =
//                new DeadLetterPublishingRecoverer(kafkaTemplate);
//
//        DefaultErrorHandler handler = new DefaultErrorHandler(recoverer, backOff);
//
//        // Retry only for certain exceptions
//        handler.addRetryableExceptions(RuntimeException.class);
//
//        // Don't retry these
//        handler.addNotRetryableExceptions(IllegalArgumentException.class);
//
//        return handler;
//    }
    private Map<String, Object> baseConsumerProps(String groupId) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, Secrets.KAFKA_BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);

        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class);

        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                JsonDeserializer.class);


        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        System.out.println(props);
        return props;
    }

    private <T> ConsumerFactory<String, T> consumerFactory(
            Class<T> clazz,
            String groupId
    ) {
        JsonDeserializer<T> deserializer = new JsonDeserializer<>(clazz);

//        deserializer.setUseTypeMapperForKey(false);

        return new DefaultKafkaConsumerFactory<>(
                baseConsumerProps(groupId),
                new StringDeserializer(),
                deserializer
        );
    }
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TranscodingSegmentUpdateDTO>
    transcodingSegmentUpdateFactory() {

        ConcurrentKafkaListenerContainerFactory<String, TranscodingSegmentUpdateDTO> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(
                consumerFactory(TranscodingSegmentUpdateDTO.class, "asset-updates-consumer")
        );

        return factory;
    }
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TranscodingFinishEventDTO>
    transcodingFinishFactory() {

        ConcurrentKafkaListenerContainerFactory<String, TranscodingFinishEventDTO> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(
                consumerFactory(TranscodingFinishEventDTO.class, "asset-updates-consumer")
        );

        return factory;
    }
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ModerationUpdateDTO>
    moderationUpdateFactory() {

        ConcurrentKafkaListenerContainerFactory<String, ModerationUpdateDTO> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(
                consumerFactory(ModerationUpdateDTO.class, "media-moderation-consumer")
        );

        return factory;
    }
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, GroupMemberCreateRequestDTO>
    groupMemberFactory() {

        ConcurrentKafkaListenerContainerFactory<String, GroupMemberCreateRequestDTO> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(
                consumerFactory(GroupMemberCreateRequestDTO.class, "group-member")
        );

        return factory;
    }
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, GroupCreateRequestDTO>
    groupCreateFactory() {

        ConcurrentKafkaListenerContainerFactory<String, GroupCreateRequestDTO> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(
                consumerFactory(GroupCreateRequestDTO.class, "group-creation")
        );

        return factory;
    }
}