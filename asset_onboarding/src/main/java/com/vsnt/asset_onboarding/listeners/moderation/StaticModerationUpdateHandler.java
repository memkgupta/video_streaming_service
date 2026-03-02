package com.vsnt.asset_onboarding.listeners.moderation;

import com.vsnt.asset_onboarding.KeyCDNService;
import com.vsnt.asset_onboarding.config.TranscodingJobMessageProducer;
import com.vsnt.asset_onboarding.dtos.ModerationStatus;
import com.vsnt.asset_onboarding.dtos.ModerationUpdateDTO;
import com.vsnt.asset_onboarding.dtos.TranscodingJob;
import com.vsnt.asset_onboarding.entities.AssetAESKey;
import com.vsnt.asset_onboarding.entities.Media;
import com.vsnt.asset_onboarding.entities.enums.MediaType;
import com.vsnt.asset_onboarding.listeners.moderation.actions.ModerationAction;
import com.vsnt.asset_onboarding.listeners.moderation.actions.ModerationActionFactory;
import com.vsnt.asset_onboarding.services.KeyService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
@Component
public class StaticModerationUpdateHandler implements ModerationUpdateHandler{
    private final TranscodingJobMessageProducer transcodingJobMessageProducer;
    private final ModerationActionFactory  moderationActionFactory;
    private final KeyService keyService;
    private final KeyCDNService keyCDNService;
    private final Executor executor;
    public StaticModerationUpdateHandler(TranscodingJobMessageProducer transcodingJobMessageProducer, ModerationActionFactory moderationActionFactory, KeyService keyService, KeyCDNService keyCDNService, @Qualifier("moderationExecutor") Executor executor) {
        this.transcodingJobMessageProducer = transcodingJobMessageProducer;
        this.moderationActionFactory = moderationActionFactory;
        this.keyService = keyService;
        this.keyCDNService = keyCDNService;
        this.executor = executor;
    }

    @Override
    public MediaType supports() {
        return MediaType.STATIC;
    }

    @Override
    public void handle(ModerationUpdateDTO update, Media media) {
        ModerationStatus status = update.getModerationStatus();
        if(status.equals(ModerationStatus.SAFE) || status.equals(ModerationStatus.REVIEW))
        {
            AssetAESKey assetKey = null;
            try {
                assetKey = keyService.getKey(update.getAssetId());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            byte[] encKey = keyCDNService.fetchSecure(
                    assetKey.getKeyURL()
            );
            String encodedKey = Base64.getEncoder().encodeToString(encKey);
            TranscodingJob job =
                    TranscodingJob.builder()
                            .key(media.getVideoAsset().getKey())
                            .encryptionKey(encodedKey)
                            .jobId(media.getId().toString())
                            .build();
            transcodingJobMessageProducer.sendMessage(job);

        }

    }
}
