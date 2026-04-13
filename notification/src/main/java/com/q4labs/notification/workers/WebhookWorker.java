package com.q4labs.notification.workers;

import com.q4labs.notification.config.RabbitMQConstants;
import com.q4labs.notification.dtos.WebhookNotification;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

@Component
public class WebhookWorker {

    private final RestTemplate restTemplate;

    public WebhookWorker() {
        this.restTemplate = new RestTemplate();
    }

    @RabbitListener(
            queues = RabbitMQConstants.WEBHOOK_QUEUE,
            containerFactory = "rabbitListenerContainerFactory"
    )
    public void consume(WebhookNotification notification) {

        try {
            sendWebhook(notification);
        } catch (Exception e) {
            System.err.println("❌ Webhook failed for event: " + notification.getId());

            // Throw exception → triggers retry + DLQ
            throw new RuntimeException(e);
        }
    }

    private void sendWebhook(WebhookNotification notification) {

        String url = notification.getWebhookUrl();

        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("Webhook URL is missing");
        }

        // Prepare headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Optional: add event type header
        headers.add("X-Event-Type", notification.getEventType());
        headers.add("X-Event-Id", notification.getId());

        HttpEntity<Object> request =
                new HttpEntity<>(notification, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                String.class
        );

        // 🚨 treat non-2xx as failure
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException(
                    "Webhook failed with status: " + response.getStatusCode()
            );
        }

        System.out.println("✅ Webhook sent successfully: " + notification.getId());
    }
}