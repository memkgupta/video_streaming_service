package com.q4labs.notification.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class RabbitMQConfig {


    @Bean
    public FanoutExchange notificationExchange() {
        return new FanoutExchange(RabbitMQConstants.FANOUT_EXCHANGE);
    }
    @Bean
    public DirectExchange dlxExchange() {
        return new DirectExchange(RabbitMQConstants.DLX_EXCHANGE);
    }
    @Bean
    public Queue webhookQueue() {
        return QueueBuilder.durable(RabbitMQConstants.WEBHOOK_QUEUE)
                .withArguments(Map.of(
                        "x-dead-letter-exchange", RabbitMQConstants.DLX_EXCHANGE,
                        "x-dead-letter-routing-key", RabbitMQConstants.WEBHOOK_DLQ
                ))
                .build();
    }
    @Bean
    public Queue webhookDLQ() {
        return QueueBuilder.durable(RabbitMQConstants.WEBHOOK_DLQ).build();
    }
    @Bean
    public Binding webhookBinding(FanoutExchange exchange, Queue webhookQueue) {
        return BindingBuilder
                .bind(webhookQueue)
                .to(exchange);
    }
    @Bean
    public Binding dlqBinding(DirectExchange dlxExchange, Queue webhookDLQ) {
        return BindingBuilder
                .bind(webhookDLQ)
                .to(dlxExchange)
                .with(RabbitMQConstants.WEBHOOK_DLQ);
    }
}