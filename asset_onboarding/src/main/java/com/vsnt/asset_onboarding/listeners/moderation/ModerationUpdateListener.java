package com.vsnt.asset_onboarding.listeners.moderation;

import com.vsnt.asset_onboarding.MessageListener;
import com.vsnt.asset_onboarding.dtos.ModerationUpdateDTO;
import com.vsnt.asset_onboarding.entities.Media;
import com.vsnt.asset_onboarding.listeners.moderation.actions.ModerationActionFactory;
import com.vsnt.asset_onboarding.services.MediaService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Component
public class ModerationUpdateListener implements MessageListener<ModerationUpdateDTO> {
    private final ModerationUpdateHandlerFactory moderationUpdateHandlerFactory;
    private final MediaService mediaService;
    private final ModerationActionFactory moderationActionFactory;

    private final Executor executor;
    public ModerationUpdateListener(ModerationUpdateHandlerFactory moderationUpdateHandlerFactory, MediaService mediaService, ModerationActionFactory moderationActionFactory,   @Qualifier("moderationExecutor") Executor executor) {
        this.moderationUpdateHandlerFactory = moderationUpdateHandlerFactory;
        this.mediaService = mediaService;
        this.moderationActionFactory = moderationActionFactory;
        this.executor = executor;
    }

    @Override
    public void onMessage(ModerationUpdateDTO message) {
        Media media = mediaService.getMedia(UUID.fromString(message.getMediaId()));
        CompletableFuture.runAsync(() ->
                        moderationActionFactory
                                .getModerationAction(message.getModerationStatus())
                                .act(message),
                executor

        );

        CompletableFuture.runAsync(() ->
                        moderationUpdateHandlerFactory
                                .getModerationUpdateHandler(media.getMediaType())
                                .handle(message, media),
                executor
        );
        //todo add persistance of moderation result
    }
}
