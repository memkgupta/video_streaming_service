package com.vsnt.asset_onboarding.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vsnt.common_lib.dtos.ModerationJob;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class ModerationJobProducer {
    private final RabbitTemplate template;
    private final ObjectMapper objectMapper = new ObjectMapper();
    public ModerationJobProducer(RabbitTemplate template) {
        this.template = template;
    }
    public void sendMessage(ModerationJob job)
    {
        try{
            template.convertAndSend(
                    RabbitMQConfig.MODERATION_JOBS,objectMapper.writeValueAsString(
                            job
                    )
            );
        }
        catch (AmqpException e)
        {
           throw new RuntimeException(
                   e.getMessage()
           );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
