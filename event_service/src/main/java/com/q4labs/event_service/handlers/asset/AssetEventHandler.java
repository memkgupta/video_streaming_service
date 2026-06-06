package com.q4labs.event_service.handlers.asset;
import com.q4labs.event_service.entities.EventLog;
import com.q4labs.event_service.services.EventLogService;
import com.vsnt.common_lib.dtos.events.asset.AssetEvent;
import com.vsnt.common_lib.dtos.events.asset.AssetEventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public abstract class AssetEventHandler<T> {
    private final EventLogService eventLogService;
    private final Logger logger =  LoggerFactory.getLogger(AssetEventHandler.class);
    protected AssetEventHandler(EventLogService eventLogService) {
        this.eventLogService = eventLogService;
    }
    abstract AssetEventType supports();
  abstract void helper(AssetEvent<T> event, EventLog eventLog);
 public void handle(AssetEvent<T> event) {
      logger.info("{}  assetId {} ",event.getEventType().name(),event.getAssetId());
      EventLog eventLog = eventLogService.saveEvent(
              event.getData(),
              event.getOrgId(),
              event.getEventType().name()
      );
      logger.info("Handling {}  assetId {} ",event.getEventType().name(),event.getAssetId());
      try{
          helper(event,eventLog);
          logger.info("Handled {}  assetId {} ",event.getEventType().name(),event.getAssetId());
      }
      catch (Exception e){
          logger.error("Error handling event assetId {} logId {} ",event.getAssetId(),eventLog.getEventId(),e);
      }
  }
}
