package com.q4labs.event_service.handlers.media;

import com.q4labs.event_service.dtos.WebhookNotification;

import com.q4labs.event_service.dtos.responses.SubscriptionResponse;
import com.q4labs.event_service.feign.UserServiceClient;
import com.q4labs.event_service.services.NotificationProducer;

import com.vsnt.common_lib.dtos.events.media.MediaEvent;
import com.vsnt.common_lib.dtos.events.media.MediaEventType;
import com.vsnt.common_lib.dtos.events.media.blocked.MediaBlockedPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


import java.util.List;

@Component
public class MediaBlockedEventHandler implements MediaEventHandler<MediaBlockedPayload> {
    private static Logger logger = LoggerFactory.getLogger(MediaBlockedEventHandler.class);
    private final NotificationProducer notificationProducer;
    private final UserServiceClient userServiceClient;
    public MediaBlockedEventHandler(NotificationProducer notificationProducer,  UserServiceClient userServiceClient) {
        this.notificationProducer = notificationProducer;
        this.userServiceClient = userServiceClient;


    }

    @Override
    public MediaEventType supports() {
        return MediaEventType.MEDIA_BLOCKED;
    }

    @Override
    public void handle(MediaEvent<MediaBlockedPayload> event) {
       logger.info("Media blocked with mediaId {}",event.getMediaId());
        List<SubscriptionResponse> whSubscription = userServiceClient.getSubscriptions(event.getOrgId(),event.getEventType().name());

        for(SubscriptionResponse subscription : whSubscription){
            notificationProducer.send(WebhookNotification.builder()
                    .id(event.getMediaId())
                    .data(event)
                    .userId(event.getOrgId())
                    .eventType(event.getEventType().name())
                    .entityId(event.getMediaId())
                    .webhookUrl(subscription.getCallbackUrl())
                    .signatureSecret(subscription.getSecret())
                    .timestamp(event.getTimestamp())
                    .source("media-service")
                    .build()
            );
            logger.info("Webhook notification sent for {} with mediaId {}",MediaEventType.MEDIA_BLOCKED,event.getMediaId());
        }
    }
}
