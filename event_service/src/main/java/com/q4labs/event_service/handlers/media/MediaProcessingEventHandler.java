package com.q4labs.event_service.handlers.media;

import com.q4labs.event_service.dtos.SSENotification;
import com.q4labs.event_service.dtos.WebhookNotification;
import com.q4labs.event_service.dtos.responses.SubscriptionResponse;
import com.q4labs.event_service.entities.EventLog;
import com.q4labs.event_service.feign.UserServiceClient;
import com.q4labs.event_service.services.EventLogService;
import com.q4labs.event_service.services.NotificationProducer;
import com.vsnt.common_lib.dtos.events.media.MediaEvent;
import com.vsnt.common_lib.dtos.events.media.MediaEventType;
import com.vsnt.common_lib.dtos.events.media.processing.MediaProcessingPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.util.List;
@Component
public class MediaProcessingEventHandler implements MediaEventHandler<MediaProcessingPayload> {
    private final UserServiceClient userServiceClient;
    private static final Logger logger = LoggerFactory.getLogger(MediaProcessingEventHandler.class);
    private final NotificationProducer notificationProducer;
    private final EventLogService eventLogService;
    public MediaProcessingEventHandler( UserServiceClient userServiceClient, NotificationProducer notificationProducer, EventLogService eventLogService) {
        this.userServiceClient = userServiceClient;
        this.notificationProducer = notificationProducer;
        this.eventLogService = eventLogService;
    }

    @Override
    public MediaEventType supports() {
        return MediaEventType.MEDIA_PROCESSING;
    }

    @Override
    public void handle(MediaEvent<MediaProcessingPayload> event) {
        logger.info("Media processing with mediaId {}",event.getMediaId());
        List<SubscriptionResponse> whSubscription = userServiceClient.getSubscriptions(event.getOrgId(),event.getEventType().name());
        EventLog eventLog = eventLogService.saveEvent(
                event.getData(),
                event.getOrgId(),
               event.getEventType().name()
        );
        for(SubscriptionResponse subscription : whSubscription){
            notificationProducer.send(WebhookNotification.builder()
                    .id(eventLog.getId())
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
            logger.info("Webhook notification sent for {} with mediaId {}",event.getEventType(),event.getMediaId());
        }
        notificationProducer.send(SSENotification.builder()
                .event_id(eventLog.getId())
                .timestamp(event.getTimestamp())
                .data(event.getData())
                .eventType(event.getEventType().name())
                .entityId(event.getMediaId())
                .build()
        );
    }
}
