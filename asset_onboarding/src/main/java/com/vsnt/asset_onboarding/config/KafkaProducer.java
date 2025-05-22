package com.vsnt.asset_onboarding.config;


import com.vsnt.asset_onboarding.dtos.UpdateRequestDTO;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class KafkaProducer {
    private final KafkaTemplate<String, UpdateRequestDTO> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, UpdateRequestDTO> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void produce(UpdateRequestDTO updateRequestDTO) {

        Message<UpdateRequestDTO> message = MessageBuilder.withPayload(updateRequestDTO)
                .setHeader(KafkaHeaders.TOPIC,"video-updates")
                .setHeader(KafkaHeaders.KEY,updateRequestDTO.getVideoId())
                .build();
        try{
            CompletableFuture<SendResult<String, UpdateRequestDTO>> future = kafkaTemplate.send(message);

            future.whenComplete((result, ex) -> {
                if (ex != null) {
                    System.out.println("Failed to send message: " + ex.getMessage());
                } else {
                    System.out.println(result.getProducerRecord());
                    System.out.println("Successfully sent to partition: " + result.getRecordMetadata().partition());
                }
            });
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
