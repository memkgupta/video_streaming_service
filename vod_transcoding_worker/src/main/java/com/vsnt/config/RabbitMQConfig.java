package com.vsnt.config;

import com.rabbitmq.client.*;
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

        logger.info("Initializing RabbitMQ connection. host={}, queue=transcoding_jobs",
                Secrets.RABBITMQ_HOST);

        while (attempt < MAX_RETRIES) {
            try {
                attempt++;

                logger.info("Connecting to RabbitMQ... attempt={}", attempt);

                ConnectionFactory factory = new ConnectionFactory();
                factory.setHost(Secrets.RABBITMQ_HOST);
                factory.setUsername(Secrets.RABBITMQ_USER);
                factory.setPassword(Secrets.RABBITMQ_PASS);


                factory.setAutomaticRecoveryEnabled(true);
                factory.setNetworkRecoveryInterval(5000);

                Connection connection = factory.newConnection();

                connection.addShutdownListener(cause ->
                        logger.error("RabbitMQ connection shutdown: {}", cause.getMessage(), cause)
                );

                Channel channel = connection.createChannel();

                channel.addShutdownListener(cause ->
                        logger.error("RabbitMQ channel shutdown: {}", cause.getMessage(), cause)
                );

                channel.basicQos(MAX_CONCURRENT_JOBS);

                channel.queueDeclare("transcoding_jobs", true, false, false, null);

                logger.info("RabbitMQ connected successfully. queue=transcoding_jobs");

                return channel;

            } catch (Exception e) {

                logger.warn("RabbitMQ connection failed. attempt={}, reason={}",
                        attempt, e.getMessage());

                if (attempt >= MAX_RETRIES) {
                    logger.error("Exceeded max retries ({}) for RabbitMQ connection", MAX_RETRIES, e);
                    throw new RuntimeException("Failed to connect to RabbitMQ", e);
                }

                logger.info("Retrying in {} ms...", delay);

                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    logger.error("Retry interrupted", ie);
                    throw new RuntimeException("Retry interrupted", ie);
                }

                delay = Math.min(delay * 2, 30000); // exponential backoff (max 30s)
            }
        }

        throw new RuntimeException("Unreachable code");
    }
}