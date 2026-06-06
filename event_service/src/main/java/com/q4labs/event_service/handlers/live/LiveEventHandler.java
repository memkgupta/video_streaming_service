package com.q4labs.event_service.handlers.live;

import com.q4labs.event_service.entities.EventLog;
import com.q4labs.event_service.services.EventLogService;
import com.vsnt.common_lib.dtos.events.live.LiveEvent;
import com.vsnt.common_lib.dtos.events.live.LiveEventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class LiveEventHandler<T>{
    private final EventLogService eventLogService;
    private static final Logger logger = LoggerFactory.getLogger(LiveEventHandler.class);
    protected LiveEventHandler(EventLogService eventLogService) {
        this.eventLogService = eventLogService;
    }

    abstract LiveEventType supports();
    abstract void helper(LiveEvent<T> event,EventLog eventLog);
    public void handle(LiveEvent<T> event)
    {
        logger.info("{}  mediaId {} ",event.getEventType().name(),event.getMediaId());
        EventLog eventLog = eventLogService.saveEvent(
                event.getData(),
              event.getOrgId(),
                event.getEventType().name()
        );
        logger.info("Handling {}  mediaId {} ",event.getEventType().name(),event.getMediaId());
        try{

            helper(event,eventLog);
            logger.info("Handled {}  mediaId {} ",event.getEventType().name(),event.getMediaId());
        }
        catch (Exception e){
            logger.error("Error handling event mediaId {} logId {} ",event.getMediaId(),eventLog.getEventId(),e);
        }
    }
}
