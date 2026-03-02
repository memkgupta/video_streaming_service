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
    private final ObjectMapper objectMapper;

    public TranscodingJobMessageProducer(
            RabbitTemplate rabbitTemplate,
            ObjectMapper objectMapper
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendMessage(TranscodingJob job) {

        try {
            System.out.println(job);
            String message =
                    objectMapper.writeValueAsString(job);
            System.out.println(message);
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.QUEUE_NAME,
                    message
            );

        } catch (Exception e) {

            // never use printStackTrace in production
            throw new RuntimeException(
                    "Failed to send transcoding job", e
            );
        }
    }
}