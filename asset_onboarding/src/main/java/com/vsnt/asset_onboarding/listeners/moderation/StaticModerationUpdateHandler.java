package com.vsnt.asset_onboarding.listeners.moderation;

import com.vsnt.asset_onboarding.config.TranscodingJobMessageProducer;
import com.vsnt.asset_onboarding.dtos.ModerationStatus;
import com.vsnt.asset_onboarding.dtos.ModerationUpdateDTO;
import com.vsnt.asset_onboarding.dtos.TranscodingJob;
import com.vsnt.asset_onboarding.entities.Media;
import com.vsnt.asset_onboarding.entities.enums.MediaType;
import com.vsnt.asset_onboarding.listeners.moderation.actions.ModerationAction;
import com.vsnt.asset_onboarding.listeners.moderation.actions.ModerationActionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
@Component
public class StaticModerationUpdateHandler implements ModerationUpdateHandler{
    private final TranscodingJobMessageProducer transcodingJobMessageProducer;
    private final ModerationActionFactory  moderationActionFactory;

    private final Executor executor;
    public StaticModerationUpdateHandler(TranscodingJobMessageProducer transcodingJobMessageProducer, ModerationActionFactory moderationActionFactory,     @Qualifier("moderationExecutor") Executor executor) {
        this.transcodingJobMessageProducer = transcodingJobMessageProducer;
        this.moderationActionFactory = moderationActionFactory;
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
            CompletableFuture.runAsync(() -> transcodingJobMessageProducer.sendMessage(
                    TranscodingJob.builder()
                            .key(media.getId().toString())
                            .jobId(update.getAssetId())

                            .build()
            ) , executor);
        }

    }
}
