package com.vsnt.asset_onboarding.config.kafka.producers;
import com.vsnt.common_lib.dtos.events.live.LiveEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class LiveEventProducer extends KafkaProducer<LiveEvent> {
    protected LiveEventProducer(KafkaTemplate<String, LiveEvent> kafkaTemplate) {
        super(kafkaTemplate, "live-updates");
    }

    @Override
    public void produceMessage(LiveEvent message) {
    kafkaTemplate.send(topic, message);
    }
}
