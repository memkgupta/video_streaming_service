package com.q4labs.event_service.workers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.q4labs.event_service.config.RabbitMQConstants;
import com.q4labs.event_service.dtos.WebhookNotification;

import com.q4labs.event_service.util.Hmac;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

@Component
public class WebhookWorker {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final Logger logger =  LoggerFactory.getLogger(WebhookWorker.class);

    public WebhookWorker(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;

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
           logger.error("Error while sending webhook notification", e);
            throw new RuntimeException(e);
        }
    }
    private void sendWebhook(WebhookNotification notification) {
        try{
            String url = notification.getWebhookUrl();
            if (url == null || url.isEmpty()) {
                throw new IllegalArgumentException("Webhook URL is missing");
            }
            // Prepare headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            //   add event type header
            headers.add("X-Event-Type", notification.getEventType());
            headers.add("X-Event-Id", notification.getId());
            headers.add("X-Webhook-Signature", Hmac.generateHmac(objectMapper.writeValueAsString(notification.getData()),
                   notification.getSignatureSecret()));
            HttpEntity<Object> request =
                    new HttpEntity<>(notification, headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    String.class
            );
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException(
                        "Webhook failed with status: " + response.getStatusCode()
                );
            }
            logger.info("Webhook sent successfully for event: " + notification.getId());
        }
        catch (Exception e){
                logger.error("Webhook failed for event: " + notification.getId(), e);
        }
    }
}