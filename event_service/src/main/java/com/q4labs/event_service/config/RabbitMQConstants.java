package com.q4labs.event_service.config;

public class RabbitMQConstants {

    public static final String NOTIFICATIONS_EXCHANGE = "notification.fanout.exchange";
    public static final String WEBHOOK_QUEUE = "notification.webhook.queue";
    public static final String WEBHOOK_DLQ = "notification.webhook.dlq";

    public static final String DLX_EXCHANGE = "notification.dlx.exchange";
}