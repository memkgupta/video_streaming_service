package com.vsnt.asset_onboarding.config;

import com.vsnt.asset_onboarding.MessageListener;
import com.vsnt.asset_onboarding.dtos.*;
import com.vsnt.asset_onboarding.dtos.media.events.TranscodingFinishEventDTO;
import com.vsnt.asset_onboarding.dtos.media.request.GroupCreateRequestDTO;
import com.vsnt.asset_onboarding.dtos.media.request.GroupMemberCreateRequestDTO;
import com.vsnt.asset_onboarding.dtos.media.response.GroupMemberDTO;
import com.vsnt.asset_onboarding.entities.Asset;
import com.vsnt.asset_onboarding.services.AssetService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {


    private final MessageListener<TranscodingSegmentUpdateDTO> transcodingSegmentUpdateListener;
    private final MessageListener<ModerationUpdateDTO> moderationUpdateListener;
    private final MessageListener<TranscodingFinishEventDTO>  transcodingFinishListener;
    public KafkaConsumer(  MessageListener<TranscodingSegmentUpdateDTO> transcodingSegmentUpdateListener, MessageListener<ModerationUpdateDTO> moderationUpdateListener, MessageListener<TranscodingFinishEventDTO> transcodingFinishListener) {


        this.transcodingSegmentUpdateListener = transcodingSegmentUpdateListener;
        this.moderationUpdateListener = moderationUpdateListener;
        this.transcodingFinishListener = transcodingFinishListener;
    }
    @KafkaListener(
            topics = "asset-transcoding-updates",
            containerFactory = "transcodingSegmentUpdateFactory"
    )
    public void listen(TranscodingSegmentUpdateDTO updateRequestDTO) {
        transcodingSegmentUpdateListener.onMessage(updateRequestDTO);
    }
    @KafkaListener(
            topics = "asset-transcoding-finish",
            containerFactory = "transcodingFinishFactory"
    )
    public void listen(TranscodingFinishEventDTO finishEventDTO) {
        transcodingFinishListener.onMessage(finishEventDTO);
    }
    @KafkaListener(
            topics = "media-moderation-update",
            containerFactory = "moderationUpdateFactory"
    )
    public void listenModerationUpdate(ModerationUpdateDTO moderationUpdateDTO) {
        moderationUpdateListener.onMessage(moderationUpdateDTO);
    }

}
