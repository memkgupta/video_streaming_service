package com.vsnt.asset_onboarding.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vsnt.asset_onboarding.dtos.SummarizationJob;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class SummarizationJobProducer {
    private final RabbitTemplate template;
    private final ObjectMapper objectMapper = new ObjectMapper();
    public SummarizationJobProducer(RabbitTemplate template) {
        this.template = template;
    }
    public void sendMessage(SummarizationJob job)
    {
        try{
            template.convertAndSend(
                    RabbitMQConfig.SUMMARIZATION_QUEUE_NAME,objectMapper.writeValueAsString(
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
