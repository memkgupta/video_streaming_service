package com.vsnt.asset_onboarding.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vsnt.asset_onboarding.dtos.TranscodingJob;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class TranscodingJobMessageProducer {
    private final RabbitTemplate rabbitTemplate;

 private final ObjectMapper objectMapper = new ObjectMapper();
    public TranscodingJobMessageProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendMessage(TranscodingJob job) {

        try {

            rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME,objectMapper.writeValueAsString(job));

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
