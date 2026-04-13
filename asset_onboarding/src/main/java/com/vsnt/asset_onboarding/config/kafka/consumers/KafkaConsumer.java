package com.vsnt.asset_onboarding.config.kafka.consumers;

import com.vsnt.asset_onboarding.MessageListener;
import com.vsnt.asset_onboarding.dtos.*;
import com.vsnt.asset_onboarding.dtos.media.events.TranscodingFinishEventDTO;
import com.vsnt.asset_onboarding.moderation.ModerationUpdateDTO;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {


    private final MessageListener<TranscodingSegmentUpdateDTO> transcodingSegmentUpdateListener;
    private final MessageListener<ModerationUpdateDTO> moderationUpdateListener;
    private final MessageListener<TranscodingFinishEventDTO>  transcodingFinishListener;
    private final MessageListener<TranscodingFailedDTO>  transcodingFailedListener;
    public KafkaConsumer(MessageListener<TranscodingSegmentUpdateDTO> transcodingSegmentUpdateListener, MessageListener<ModerationUpdateDTO> moderationUpdateListener, MessageListener<TranscodingFinishEventDTO> transcodingFinishListener, MessageListener<TranscodingFailedDTO> transcodingFailedListener) {
        this.transcodingSegmentUpdateListener = transcodingSegmentUpdateListener;
        this.moderationUpdateListener = moderationUpdateListener;
        this.transcodingFinishListener = transcodingFinishListener;
        this.transcodingFailedListener = transcodingFailedListener;
    }
    @KafkaListener(
            topics = "asset-transcoding-updates",
            containerFactory = "transcodingSegmentUpdateFactory"
    )
    public void listen(TranscodingSegmentUpdateDTO updateRequestDTO) {
        transcodingSegmentUpdateListener.onMessage(updateRequestDTO);
    }
    @KafkaListener(
            topics = "asset-transcoding-fail",
            containerFactory = "transcodingFinishFactory"
    )
    public void listen(TranscodingFailedDTO transcodingFailedDTO) {
        transcodingFailedListener.onMessage(transcodingFailedDTO);
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
        System.out.println("Message "+moderationUpdateDTO);
        moderationUpdateListener.onMessage(moderationUpdateDTO);
    }

}
