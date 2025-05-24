package com.vsnt.user.config;

import com.vsnt.user.payload.ChannelPayload;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class KafkaProducer {
    private final KafkaTemplate<String, ChannelPayload> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, ChannelPayload> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void produce(ChannelPayload payload)
    {
        Message<ChannelPayload> message = MessageBuilder.withPayload(payload)
                .setHeader(KafkaHeaders.TOPIC,"create-channel")
                .build();
        try{
            CompletableFuture<SendResult<String, ChannelPayload>> future = kafkaTemplate.send(message);

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
