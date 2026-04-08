package com.vsnt.asset_onboarding.config.kafka.producers;

import com.vsnt.asset_onboarding.dtos.media.notification.BlockMedia;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class BlockMediaProducer extends KafkaProducer<BlockMedia> {
    protected BlockMediaProducer(KafkaTemplate<String, BlockMedia> kafkaTemplate) {
        super(kafkaTemplate,"block-media");
    }
    @Override
    public void produceMessage(BlockMedia message) {
    kafkaTemplate.send(topic, message.getMediaId(),message );
    }
}
