package com.vsnt.config;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class RabbitMQConfig {

    private static Connection connection;

    static {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("rabbitmq_q4");
            factory.setPort(5672);
            factory.setUsername("guest");
            factory.setPassword("guest");

            // Optional tuning
            factory.setAutomaticRecoveryEnabled(true);

            connection = factory.newConnection();

        } catch (Exception e) {
            throw new RuntimeException("Failed to create RabbitMQ connection", e);
        }
    }

    public static Connection getConnection() {
        return connection;
    }
}
