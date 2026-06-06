package com.q4labs.event_service.handlers.live;
import com.q4labs.event_service.dtos.WebhookNotification;
import com.q4labs.event_service.dtos.responses.SubscriptionResponse;
import com.q4labs.event_service.entities.EventLog;
import com.q4labs.event_service.feign.UserServiceClient;
import com.q4labs.event_service.services.EventLogService;
import com.q4labs.event_service.services.NotificationProducer;
import com.vsnt.common_lib.dtos.events.live.LiveEvent;
import com.vsnt.common_lib.dtos.events.live.LiveEventType;
import com.vsnt.common_lib.dtos.events.live.start.LiveStartedPayload;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class LiveStartEventHandler extends LiveEventHandler<LiveStartedPayload> {
    private final UserServiceClient userServiceClient;
    private final NotificationProducer notificationProducer;
    protected LiveStartEventHandler(EventLogService eventLogService,  UserServiceClient userServiceClient, NotificationProducer notificationProducer) {
        super(eventLogService);
        this.userServiceClient = userServiceClient;

        this.notificationProducer = notificationProducer;
    }

    @Override
    public LiveEventType supports() {
        return LiveEventType.LIVE_STARTED;
    }

    @Override
    public void helper(LiveEvent<LiveStartedPayload> event, EventLog eventLog) {
        List<SubscriptionResponse> subscriptions =userServiceClient.getSubscriptions(
                eventLog.getOrgId(),
                eventLog.getEventType()
        );
        for(SubscriptionResponse subscription : subscriptions){
            notificationProducer.send(
                    WebhookNotification.builder()
                            .data(event.getData())
                            .eventType(eventLog.getEventType())
                            .entityId(event.getMediaId())
                            .id(eventLog.getId())
                            .timestamp(event.getTimestamp())
                            .signatureSecret(subscription.getSecret())
                            .webhookUrl(subscription.getCallbackUrl())
                            .build()
            );
        }
    }
}
