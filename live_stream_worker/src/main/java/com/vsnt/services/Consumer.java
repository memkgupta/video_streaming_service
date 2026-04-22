package com.vsnt.services;

import com.rabbitmq.client.*;
import com.vsnt.config.RabbitMQConfig;
import com.vsnt.config.Secrets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public class Consumer {

    private static final Logger logger = LoggerFactory.getLogger(Consumer.class);

    private final RabbitMQConfig config;
    private final JobProcessor processor;
    private final ExecutorService executor;
    private final StreamManager streamManager;

    public Consumer(RabbitMQConfig config,
                    JobProcessor processor,
                    ExecutorService executor,
                    StreamManager streamManager) {
        this.config = config;
        this.processor = processor;
        this.executor = executor;
        this.streamManager = streamManager;
    }

    public void start() throws Exception {
        logger.info("Starting RabbitMQ consumer... queue={}", Secrets.RABBITMQ_QUEUE);

        Channel channel = config.getChannel();

        channel.basicQos(1);
        logger.info("QoS set to 1 (one job per worker)");

        DeliverCallback callback = (tag, delivery) -> {

            long deliveryTag = delivery.getEnvelope().getDeliveryTag();

            Map<String, Object> headers =
                    delivery.getProperties().getHeaders() != null
                            ? delivery.getProperties().getHeaders()
                            : new HashMap<>();

            logger.debug("Received message. deliveryTag={}, headers={}", deliveryTag, headers);

            if (!streamManager.canConsume()) {
                logger.warn("Worker STOPPING → requeue message. deliveryTag={}", deliveryTag);
                channel.basicNack(deliveryTag, false, true);
                return;
            }

            executor.submit(() -> {
                try {
                    logger.info("Processing job. deliveryTag={}", deliveryTag);

                    processor.process(
                            channel,
                            deliveryTag,
                            delivery.getBody(),
                            headers
                    );

                    logger.info("Job submitted to processor. deliveryTag={}", deliveryTag);

                } catch (Exception e) {
                    logger.error("Error processing job. deliveryTag={}", deliveryTag, e);

                    try {
                        logger.warn("Requeueing failed job. deliveryTag={}", deliveryTag);
                        channel.basicNack(deliveryTag, false, true);
                    } catch (Exception ex) {
                        logger.error("Failed to nack message. deliveryTag={}", deliveryTag, ex);
                    }
                }
            });
        };

        channel.basicConsume(Secrets.RABBITMQ_QUEUE, false, callback, tag -> {
            logger.warn("Consumer cancelled. consumerTag={}", tag);
        });

        logger.info("Consumer successfully started and listening for messages");
    }
}