package com.vsnt.config;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;

import java.util.concurrent.TimeoutException;

import static com.vsnt.config.Secrets.MAX_CONCURRENT_JOBS;

public class RabbitMQConfig {
    public Channel getChannel() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(Secrets.RABBITMQ_HOST);
        factory.setUsername(Secrets.RABBITMQ_USER);
        factory.setPassword(Secrets.RABBITMQ_PASS);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.basicQos(MAX_CONCURRENT_JOBS);
        channel.queueDeclare("transcoding_jobs", true, false, false, null);
        return channel;
    }
}
