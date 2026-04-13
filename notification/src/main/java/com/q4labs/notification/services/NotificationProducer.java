package com.q4labs.notification.services;
import com.q4labs.notification.config.RabbitMQConstants;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationProducer {

    private final RabbitTemplate rabbitTemplate;

    public NotificationProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void send(Object notification) {
        rabbitTemplate.convertAndSend(
                RabbitMQConstants.FANOUT_EXCHANGE,
                "",
                notification
        );
    }
}
