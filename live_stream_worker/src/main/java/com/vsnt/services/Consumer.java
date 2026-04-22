package com.vsnt.services;

import com.rabbitmq.client.*;
import com.vsnt.config.RabbitMQConfig;
import com.vsnt.config.Secrets;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public class Consumer {
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
        System.out.println("Started consuming");
        Channel channel = config.getChannel();

        // limit in-flight work per consumer
        channel.basicQos(1);

        DeliverCallback callback = (tag, delivery) -> {

            Map<String, Object> headers =
                    delivery.getProperties().getHeaders() != null
                            ? delivery.getProperties().getHeaders()
                            : new HashMap<>();

            long deliveryTag = delivery.getEnvelope().getDeliveryTag();


            if (!streamManager.canConsume()) {
                System.out.println("Worker STOPPING → skip & ACK");
                channel.basicNack(deliveryTag, false, true);
                return;
            }

            // 🔥 Process asynchronously
            executor.submit(() -> {
                try {
                    processor.process(
                            channel,
                            deliveryTag,
                            delivery.getBody(),
                            headers
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        // safety: retry
                        channel.basicNack(deliveryTag, false, true);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        };

        channel.basicConsume(Secrets.RABBITMQ_QUEUE, false, callback, tag -> {});
    }
}