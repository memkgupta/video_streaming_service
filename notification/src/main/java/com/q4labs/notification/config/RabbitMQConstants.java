package com.q4labs.notification.config;

public class RabbitMQConstants {

    public static final String FANOUT_EXCHANGE = "notification.fanout.exchange";
    public static final String WEBHOOK_QUEUE = "notification.webhook.queue";
    public static final String WEBHOOK_DLQ = "notification.webhook.dlq";

    public static final String DLX_EXCHANGE = "notification.dlx.exchange";
}