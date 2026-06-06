package com.q4labs.event_service.handlers.asset;

import com.q4labs.event_service.dtos.SSENotification;
import com.q4labs.event_service.entities.EventLog;
import com.q4labs.event_service.services.EventLogService;
import com.vsnt.common_lib.dtos.events.asset.AssetEvent;
import com.vsnt.common_lib.dtos.events.asset.AssetEventType;
import com.vsnt.common_lib.dtos.events.asset.transcoding.AssetTranscodingFailurePayload;
import org.springframework.stereotype.Component;

@Component
public class AssetTranscodingFailedHandler extends AssetEventHandler<AssetTranscodingFailurePayload> {
    protected AssetTranscodingFailedHandler(EventLogService eventLogService) {
        super(eventLogService);
    }

    @Override
    public AssetEventType supports() {
        return AssetEventType.ASSET_TRANSCODING_FAILED;
    }

    @Override
    public void helper(AssetEvent<AssetTranscodingFailurePayload> event, EventLog eventLog) {

    }
}
