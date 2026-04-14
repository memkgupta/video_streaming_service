package com.vsnt.asset_onboarding.listeners.moderation;
import com.vsnt.asset_onboarding.SecuredCDNService;
import com.vsnt.asset_onboarding.config.TranscodingJobMessageProducer;
import com.vsnt.asset_onboarding.config.kafka.producers.MediaUpdateProducer;
import com.vsnt.asset_onboarding.dtos.ModerationStatus;
import com.vsnt.asset_onboarding.dtos.media.notification.MediaStatusUpdate;
import com.vsnt.asset_onboarding.dtos.notification.Notification;
import com.vsnt.asset_onboarding.entities.enums.MediaStatus;
import com.vsnt.asset_onboarding.moderation.ModerationUpdateDTO;
import com.vsnt.asset_onboarding.dtos.TranscodingJob;
import com.vsnt.asset_onboarding.entities.AssetAESKey;
import com.vsnt.asset_onboarding.entities.Media;
import com.vsnt.asset_onboarding.entities.enums.MediaType;
import com.vsnt.asset_onboarding.listeners.moderation.actions.ModerationActionFactory;
import com.vsnt.asset_onboarding.services.KeyService;
import com.vsnt.asset_onboarding.services.MediaService;
import com.vsnt.common_lib.dtos.events.media.blocked.MediaBlockedEvent;
import com.vsnt.common_lib.dtos.events.media.blocked.MediaBlockedPayload;
import com.vsnt.common_lib.dtos.events.media.processing.MediaProcessingEvent;
import com.vsnt.common_lib.dtos.events.media.review.MediaReviewPayload;
import com.vsnt.common_lib.dtos.events.media.review.MediaUnderReviewEvent;
import com.vsnt.common_lib.enums.AssetType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.Executor;
@Component
public class StaticModerationUpdateHandler implements ModerationUpdateHandler{
    private final TranscodingJobMessageProducer transcodingJobMessageProducer;
    private final ModerationActionFactory  moderationActionFactory;
    private final KeyService keyService;
    private final SecuredCDNService securedCDNService;
    private final Executor executor;
    private final MediaUpdateProducer mediaUpdateProducer;
    private final MediaService mediaService;

    public StaticModerationUpdateHandler(TranscodingJobMessageProducer transcodingJobMessageProducer, ModerationActionFactory moderationActionFactory, KeyService keyService, SecuredCDNService securedCDNService, @Qualifier("moderationExecutor") Executor executor,  MediaUpdateProducer mediaUpdateProducer, MediaService mediaService) {
        this.transcodingJobMessageProducer = transcodingJobMessageProducer;
        this.moderationActionFactory = moderationActionFactory;
        this.keyService = keyService;
        this.securedCDNService = securedCDNService;
        this.executor = executor;
        this.mediaUpdateProducer = mediaUpdateProducer;

        this.mediaService = mediaService;
    }

    @Override
    public MediaType supports() {
        return MediaType.STATIC;
    }

    @Override
    public void handle(ModerationUpdateDTO update, Media media) {
        ModerationStatus status = update.getModerationStatus();
        if(!media.isActive())
        {
            return;
        }
        if(status.equals(ModerationStatus.APPROVED) || status.equals(ModerationStatus.FLAGGED))
        {
            if(status.equals(ModerationStatus.FLAGGED))
            {
                mediaUpdateProducer.produceMessage(new MediaUnderReviewEvent(
                        media.getId().toString(),
                        Instant.now(),
                        media.getOrgId(),
                        MediaReviewPayload.builder()
                                .details( update.getModerationResult())
                                .assetType(AssetType.VIDEO)
                                .assetId(update.getAssetId())
                                .build()
                ));
            }
            AssetAESKey assetKey = null;
            try {
                assetKey = keyService.getKey(update.getAssetId());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            byte[] encKey = securedCDNService.fetchSecure(
                    assetKey.getKeyURL()
            );
            String encodedKey = Base64.getEncoder().encodeToString(encKey);
            TranscodingJob job =
                    TranscodingJob.builder()
                            .key(media.getVideoAsset().getKey())
                            .encryptionKey(encodedKey)
                            .assetId(media.getVideoAsset().getId().toString())
                            .jobId(media.getId().toString())
                            .build();
            transcodingJobMessageProducer.sendMessage(job);

        }
        else {
           mediaUpdateProducer.produceMessage(new MediaBlockedEvent(
                   media.getId().toString(),
                   Instant.now(),
                   media.getOrgId(),
                   MediaBlockedPayload.builder()
                           .details( update.getModerationResult())
                           .assetType(AssetType.VIDEO)
                           .assetId(update.getAssetId())

                           .build()
           ));
            media.setActive(false);
            mediaService.save(media);
        }
    }
}
