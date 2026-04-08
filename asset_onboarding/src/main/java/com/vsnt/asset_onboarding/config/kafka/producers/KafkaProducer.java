package com.vsnt.asset_onboarding.config.kafka.producers;

import org.springframework.kafka.core.KafkaTemplate;


public abstract class KafkaProducer<T> {
    protected final KafkaTemplate<String, T> kafkaTemplate;
    protected final String topic;
    protected KafkaProducer(KafkaTemplate<String, T> kafkaTemplate, String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }
    public abstract void produceMessage(T message);
}
