package com.q4labs.event_service.handlers.asset;

import com.q4labs.event_service.dtos.SSENotification;
import com.q4labs.event_service.entities.EventLog;
import com.q4labs.event_service.services.EventLogService;
import com.q4labs.event_service.services.NotificationProducer;
import com.vsnt.common_lib.dtos.events.asset.AssetEvent;
import com.vsnt.common_lib.dtos.events.asset.AssetEventType;
import com.vsnt.common_lib.dtos.events.asset.transcoding.AssetTranscodingCompletedPayload;
import org.springframework.stereotype.Component;

@Component
public class AssetTranscodingCompletedHandler extends   AssetEventHandler<AssetTranscodingCompletedPayload> {
    private final NotificationProducer notificationProducer;
    protected AssetTranscodingCompletedHandler(EventLogService eventLogService, NotificationProducer notificationProducer) {
        super(eventLogService);
        this.notificationProducer = notificationProducer;
    }

    @Override
    public AssetEventType supports() {
        return AssetEventType.ASSET_TRANSCODING_COMPLETED;
    }

    @Override
    public void helper(AssetEvent<AssetTranscodingCompletedPayload> event, EventLog eventLog) {
       notificationProducer.send( SSENotification.builder()
               .event_id(eventLog.getEventId())
               .data(event.getData())
               .entityId(event.getAssetId())
               .timestamp(event.getTimestamp())
               .source("asset-service")
               .eventType(eventLog.getEventType())
               .build());
    }
}
