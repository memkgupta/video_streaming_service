package com.vsnt.config;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.vsnt.config.Secrets.MAX_CONCURRENT_JOBS;

public class RabbitMQConfig {

    private static final int MAX_RETRIES = 10;
    private static final int INITIAL_DELAY_MS = 2000; // 2 sec

    public Channel getChannel() throws IOException, TimeoutException {
        int attempt = 0;
        int delay = INITIAL_DELAY_MS;

        while (attempt < MAX_RETRIES) {
            try {
                ConnectionFactory factory = new ConnectionFactory();
                factory.setHost(Secrets.RABBITMQ_HOST);
                factory.setUsername(Secrets.RABBITMQ_USER);
                factory.setPassword(Secrets.RABBITMQ_PASS);

                // Optional but recommended
                factory.setAutomaticRecoveryEnabled(true);
                factory.setNetworkRecoveryInterval(5000);

                Connection connection = factory.newConnection();
                Channel channel = connection.createChannel();

                channel.basicQos(MAX_CONCURRENT_JOBS);
                channel.queueDeclare(Secrets.RABBITMQ_QUEUE, true, false, false, null);

                System.out.println("Connected to RabbitMQ");
                return channel;

            } catch (Exception e) {
                attempt++;
                System.out.println("RabbitMQ connection failed (attempt " + attempt + "): " + e.getMessage());

                if (attempt >= MAX_RETRIES) {
                    throw new RuntimeException("Failed to connect to RabbitMQ after retries", e);
                }

                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Retry interrupted", ie);
                }

                // Exponential backoff (max 30 sec)
                delay = Math.min(delay * 2, 30000);
            }
        }

        throw new RuntimeException("Unreachable code");
    }
}