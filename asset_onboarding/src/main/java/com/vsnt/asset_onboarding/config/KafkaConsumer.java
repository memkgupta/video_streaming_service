package com.vsnt.asset_onboarding.config;

import com.vsnt.asset_onboarding.dtos.UpdateRequestDTO;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {
    @KafkaListener(topics = "updates",groupId = "consumer-group-id")
    public void listen(Message<UpdateRequestDTO> message) {
        System.out.println(message.getPayload());
    }
}
