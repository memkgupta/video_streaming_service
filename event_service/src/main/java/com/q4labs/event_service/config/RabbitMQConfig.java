package com.q4labs.event_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class RabbitMQConfig {
    @Bean
    public MessageConverter jacksonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter) {

        RabbitTemplate rabbitTemplate =
                new RabbitTemplate(connectionFactory);

        rabbitTemplate.setMessageConverter(messageConverter);

        return rabbitTemplate;
    }
    @Bean
    public DirectExchange notificationExchange() {
        //todo add routing rules
        return new DirectExchange(RabbitMQConstants.NOTIFICATIONS_EXCHANGE);
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
    public Binding webhookBinding(@Qualifier("notificationExchange") DirectExchange exchange, Queue webhookQueue) {
        return BindingBuilder
                .bind(webhookQueue)
                .to(exchange)
                .with("notifications.webhook");
    }
    @Bean
    public Binding dlqBinding(DirectExchange dlxExchange, Queue webhookDLQ) {
        return BindingBuilder
                .bind(webhookDLQ)
                .to(dlxExchange)
                .with(RabbitMQConstants.WEBHOOK_DLQ);
    }
}