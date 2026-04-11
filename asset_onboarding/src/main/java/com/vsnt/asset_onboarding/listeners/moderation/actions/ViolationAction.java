package com.vsnt.asset_onboarding.listeners.moderation.actions;

import com.vsnt.asset_onboarding.dtos.ModerationStatus;
import com.vsnt.asset_onboarding.entities.Media;
import com.vsnt.asset_onboarding.entities.enums.MediaStatus;
import com.vsnt.asset_onboarding.moderation.ModerationUpdateDTO;
import com.vsnt.asset_onboarding.services.MediaService;
import com.vsnt.asset_onboarding.services.ModerationKVService;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ViolationAction implements ModerationAction{
    private final ModerationKVService moderationKVService;
    private final long THRESHOLD = 2;
    private final MediaService mediaService;

    public ViolationAction(ModerationKVService moderationKVService, MediaService mediaService) {
        this.moderationKVService = moderationKVService;
        this.mediaService = mediaService;
    }

    @Override
    public MediaStatus act(ModerationUpdateDTO dto, Media media) {
    //todo maintain a violation counter and also notify the org admin , once max violation reach instantly block the media
    long nc = moderationKVService.increment(dto.getJobId(),dto.getViolationCount());
    if(nc>THRESHOLD){
        //block the video
        mediaService.blockMedia(UUID.fromString(dto.getJobId()),"Moderation violation");
        return MediaStatus.BLOCKED;
    }
    return media.getStatus();
    }

    @Override
    public ModerationStatus support() {
        return ModerationStatus.REJECTED;
    }
}
