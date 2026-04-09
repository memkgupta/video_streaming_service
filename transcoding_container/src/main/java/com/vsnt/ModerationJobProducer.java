package com.vsnt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import com.vsnt.config.RabbitMQConfig;
import com.vsnt.dtos.ModerationJob;

import java.nio.charset.StandardCharsets;

public class ModerationJobProducer {

    private static final String QUEUE_NAME = "moderation_queue";

    private final Channel channel;
    private final ObjectMapper objectMapper;

    public ModerationJobProducer() {
        try {
            this.channel = RabbitMQConfig.getConnection().createChannel();
            this.objectMapper = new ObjectMapper();

            // Declare queue (idempotent)
            channel.queueDeclare(
                    QUEUE_NAME,
                    true,   // durable
                    false,
                    false,
                    null
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to init producer", e);
        }
    }

    public void send(ModerationJob job) {
        try {
            String message = objectMapper.writeValueAsString(job);

            AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
                    .contentType("application/json")
                    .deliveryMode(2) // persistent
                    .build();

            channel.basicPublish(
                    "",              // default exchange
                    QUEUE_NAME,      // routing key = queue
                    props,
                    message.getBytes(StandardCharsets.UTF_8)
            );

            System.out.println("Sent ModerationJob → " + message);

        } catch (Exception e) {
            throw new RuntimeException("Failed to publish message", e);
        }
    }

    public void close() {
        try {
            channel.close();
        } catch (Exception ignored) {}
    }
}