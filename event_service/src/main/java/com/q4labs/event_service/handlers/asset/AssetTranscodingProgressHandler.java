package com.q4labs.event_service.handlers.asset;

import com.q4labs.event_service.dtos.SSENotification;
import com.q4labs.event_service.entities.EventLog;
import com.q4labs.event_service.services.EventLogService;
import com.q4labs.event_service.services.NotificationProducer;
import com.vsnt.common_lib.dtos.events.asset.AssetEvent;
import com.vsnt.common_lib.dtos.events.asset.AssetEventType;
import com.vsnt.common_lib.dtos.events.asset.transcoding.AssetTranscodingProgressPayload;

public class AssetTranscodingProgressHandler extends AssetEventHandler<AssetTranscodingProgressPayload> {
    private final NotificationProducer notificationProducer;
    protected AssetTranscodingProgressHandler(EventLogService eventLogService, NotificationProducer notificationProducer) {
        super(eventLogService);
        this.notificationProducer = notificationProducer;
    }

    @Override
    public AssetEventType supports() {
        return AssetEventType.ASSET_TRANSCODING_PROGRESS;
    }

    @Override
    public void helper(AssetEvent<AssetTranscodingProgressPayload> event, EventLog eventLog) {
       notificationProducer.send(
               SSENotification.builder()
                       .event_id(eventLog.getEventId())
                       .data(event.getData())
                       .entityId(event.getAssetId())
                       .timestamp(event.getTimestamp())
                       .source("asset-service")
                       .eventType(eventLog.getEventType())
                       .build()
       );
    }
}
