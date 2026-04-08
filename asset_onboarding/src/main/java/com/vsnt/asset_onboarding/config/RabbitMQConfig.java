package com.vsnt.asset_onboarding.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;


public class RabbitMQConfig {
    static final String QUEUE_NAME = "transcoding_jobs" ;
    static final String MODERATION_JOBS = "moderation_queue" ;
    @Bean
    public Queue transcodingQueue() {
        return new Queue(QUEUE_NAME, false);
    }
    @Bean
    public Queue moderationJobsQueue() {
        return new Queue(MODERATION_JOBS,false);
    }
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,ObjectMapper objectMapper) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(new Jackson2JsonMessageConverter(objectMapper));
        return template;
    }

}

