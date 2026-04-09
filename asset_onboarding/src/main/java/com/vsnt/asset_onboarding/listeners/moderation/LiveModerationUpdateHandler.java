package com.vsnt.asset_onboarding.listeners.moderation;

import com.vsnt.asset_onboarding.config.kafka.producers.BlockMediaProducer;
import com.vsnt.asset_onboarding.config.kafka.producers.NotificationMediaStatusUpdateProducer;
import com.vsnt.asset_onboarding.dtos.ModerationStatus;
import com.vsnt.asset_onboarding.dtos.media.notification.BlockMedia;
import com.vsnt.asset_onboarding.dtos.media.notification.MediaStatusUpdate;
import com.vsnt.asset_onboarding.dtos.notification.Notification;
import com.vsnt.asset_onboarding.entities.enums.MediaStatus;
import com.vsnt.asset_onboarding.moderation.ModerationUpdateDTO;
import com.vsnt.asset_onboarding.entities.Media;
import com.vsnt.asset_onboarding.entities.enums.MediaType;
import com.vsnt.asset_onboarding.listeners.moderation.actions.ModerationActionFactory;
import com.vsnt.asset_onboarding.services.MediaService;
import com.vsnt.asset_onboarding.services.ModerationKVService;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Component
public class LiveModerationUpdateHandler implements ModerationUpdateHandler{
    private final ModerationActionFactory moderationActionFactory;
    private final BlockMediaProducer blockMediaProducer;
    private final MediaService mediaService;
    private final NotificationMediaStatusUpdateProducer notificationMediaStatusUpdateProducer;
    private final ModerationKVService moderationKVService;

    public LiveModerationUpdateHandler(ModerationActionFactory moderationActionFactory, BlockMediaProducer blockMediaProducer, MediaService mediaService, NotificationMediaStatusUpdateProducer notificationMediaStatusUpdateProducer, ModerationKVService moderationKVService) {
        this.moderationActionFactory = moderationActionFactory;
        this.blockMediaProducer = blockMediaProducer;
        this.mediaService = mediaService;
        this.notificationMediaStatusUpdateProducer = notificationMediaStatusUpdateProducer;
        this.moderationKVService = moderationKVService;
    }

    @Override
    public MediaType supports() {
        return MediaType.LIVE;
    }

    @Override
    public void handle(ModerationUpdateDTO update, Media media) {
        if(update.getModerationStatus().equals(ModerationStatus.REJECTED))
        {
            moderationKVService.increment(media.getId().toString(),update.getViolationCount());
         if(moderationKVService.getViolationCount(update.getJobId())>10)
         {
             media.setStatus(MediaStatus.BLOCKED);
             blockMediaProducer.produceMessage(BlockMedia.builder()
                     .mediaId(media.getId().toString())
                     .timestamp(Instant.now())
                     .build());
             notificationMediaStatusUpdateProducer.produceMessage(
                     Notification.<MediaStatusUpdate>builder()
                             .orgId(media.getOrgId())
                             .notificationId(UUID.randomUUID().toString())
                             .message(MediaStatusUpdate.builder()
                                     .mediaStatus(MediaStatus.BLOCKED)
                                     .message(Map.of("message","Media blocked due to NSFW violation"))
                                     .mediaType(MediaType.LIVE)
                                     .createdAt(Instant.now())
                                     .mediaId(media.getId().toString())

                                     .build())
                             .build()
             );
             mediaService.save(media);
         }

        }

    }
}
