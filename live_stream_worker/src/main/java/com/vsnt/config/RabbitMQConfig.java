package com.vsnt.config;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.vsnt.config.Secrets.MAX_CONCURRENT_JOBS;

public class RabbitMQConfig {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQConfig.class);

    private static final int MAX_RETRIES = 10;
    private static final int INITIAL_DELAY_MS = 2000;

    public Channel getChannel() throws IOException, TimeoutException {
        int attempt = 0;
        int delay = INITIAL_DELAY_MS;

        logger.info("Initializing RabbitMQ connection... host={}, queue={}",
                Secrets.RABBITMQ_HOST, Secrets.RABBITMQ_QUEUE);

        while (attempt < MAX_RETRIES) {
            try {
                attempt++;

                logger.info("Attempt {} to connect to RabbitMQ", attempt);

                ConnectionFactory factory = new ConnectionFactory();
                factory.setHost(Secrets.RABBITMQ_HOST);
                factory.setUsername(Secrets.RABBITMQ_USER);
                factory.setPassword(Secrets.RABBITMQ_PASS);

                factory.setAutomaticRecoveryEnabled(true);
                factory.setNetworkRecoveryInterval(5000);

                Connection connection = factory.newConnection();
                Channel channel = connection.createChannel();

                channel.basicQos(MAX_CONCURRENT_JOBS);
                channel.queueDeclare(Secrets.RABBITMQ_QUEUE, true, false, false, null);

                logger.info("Successfully connected to RabbitMQ. Queue declared: {}",
                        Secrets.RABBITMQ_QUEUE);

                return channel;

            } catch (Exception e) {

                logger.warn("RabbitMQ connection failed on attempt {}. Reason: {}",
                        attempt, e.getMessage());

                if (attempt >= MAX_RETRIES) {
                    logger.error("Failed to connect to RabbitMQ after {} attempts", attempt, e);
                    throw new RuntimeException("Failed to connect to RabbitMQ after retries", e);
                }

                logger.info("Retrying in {} ms...", delay);

                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    logger.error("Retry sleep interrupted", ie);
                    throw new RuntimeException("Retry interrupted", ie);
                }

                delay = Math.min(delay * 2, 30000); // exponential backoff
            }
        }

        logger.error("Reached unreachable code in RabbitMQConfig");
        throw new RuntimeException("Unreachable code");
    }
}