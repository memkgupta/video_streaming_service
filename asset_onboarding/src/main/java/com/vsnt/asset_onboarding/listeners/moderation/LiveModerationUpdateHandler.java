package com.vsnt.asset_onboarding.listeners.moderation;

import com.vsnt.asset_onboarding.config.kafka.producers.BlockMediaProducer;
import com.vsnt.asset_onboarding.config.kafka.producers.MediaUpdateProducer;
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
import com.vsnt.common_lib.dtos.events.media.blocked.MediaBlockedEvent;
import com.vsnt.common_lib.dtos.events.media.blocked.MediaBlockedPayload;
import com.vsnt.common_lib.enums.AssetType;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Component
public class LiveModerationUpdateHandler implements ModerationUpdateHandler{

    private final MediaService mediaService;
    private final MediaUpdateProducer mediaUpdateProducer;
    private final ModerationKVService moderationKVService;

    public LiveModerationUpdateHandler(ModerationActionFactory moderationActionFactory, BlockMediaProducer blockMediaProducer, MediaService mediaService, MediaUpdateProducer mediaUpdateProducer, ModerationKVService moderationKVService) {

        this.mediaService = mediaService;
        this.mediaUpdateProducer = mediaUpdateProducer;
        this.moderationKVService = moderationKVService;
    }

    @Override
    public MediaType supports() {
        return MediaType.LIVE;
    }

    @Override
    public void handle(ModerationUpdateDTO update, Media media) {
        if(!media.isActive()) return;
        if(media.getStatus().equals(MediaStatus.BLOCKED))
        {

            mediaUpdateProducer.produceMessage(
                    new MediaBlockedEvent(
                            media.getId().toString(), Instant.now(), media.getOrgId(), MediaBlockedPayload.builder()
                            .assetId(update.getAssetId())
                            .assetType(AssetType.LIVE_VIDEO)
                            .details(update.getModerationResult())
                            .build()
                    )
            );
            media.setActive(false);
            mediaService.save(media);
        }



    }
}
