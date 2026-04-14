package com.vsnt.asset_onboarding.config.kafka.producers;

import com.vsnt.common_lib.dtos.events.media.MediaEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class MediaUpdateProducer extends KafkaProducer<MediaEvent>{
    protected MediaUpdateProducer(KafkaTemplate<String, MediaEvent> kafkaTemplate) {
        super(kafkaTemplate, "media-updates");
    }

    @Override
    public void produceMessage(MediaEvent message) {
    kafkaTemplate.send(topic, message);
    }
}
