package com.vsnt.asset_onboarding.config;

import com.vsnt.asset_onboarding.MessageListener;
import com.vsnt.asset_onboarding.dtos.*;
import com.vsnt.asset_onboarding.dtos.media.request.GroupCreateRequestDTO;
import com.vsnt.asset_onboarding.dtos.media.request.GroupMemberCreateRequestDTO;
import com.vsnt.asset_onboarding.dtos.media.response.GroupMemberDTO;
import com.vsnt.asset_onboarding.entities.Asset;
import com.vsnt.asset_onboarding.services.AssetService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {
    private final AssetService assetService;

    private final MessageListener<GroupMemberCreateRequestDTO> messageListener;
    private final MessageListener<GroupCreateRequestDTO> groupMemberListener;
    private final MessageListener<TranscodingSegmentUpdateDTO> transcodingSegmentUpdateListener;
    private final MessageListener<ModerationUpdateDTO> moderationUpdateListener;
    public KafkaConsumer(AssetService assetService,MessageListener<GroupMemberCreateRequestDTO> messageListener, MessageListener<GroupCreateRequestDTO> groupMemberListener, MessageListener<TranscodingSegmentUpdateDTO> transcodingSegmentUpdateListener, MessageListener<ModerationUpdateDTO> moderationUpdateListener) {
        this.assetService = assetService;
        this.messageListener = messageListener;
        this.groupMemberListener = groupMemberListener;
        this.transcodingSegmentUpdateListener = transcodingSegmentUpdateListener;
        this.moderationUpdateListener = moderationUpdateListener;
    }
    @KafkaListener(topics = "asset-transcoding-updates",groupId = "asset-updates-consumer")
    public void listen(TranscodingSegmentUpdateDTO updateRequestDTO) {
        transcodingSegmentUpdateListener.onMessage(updateRequestDTO);
        }
    @KafkaListener(topics="media-moderation-update",groupId = "media-moderation-consumer")
    public void listenModerationUpdate(ModerationUpdateDTO moderationUpdateDTO) {
      moderationUpdateListener.onMessage(moderationUpdateDTO);
    }
    @KafkaListener(topics = "group-member-notification",groupId = "group-member")
    public void listen(GroupMemberCreateRequestDTO member) {
        messageListener.onMessage(member);
    }
    @KafkaListener(topics = "group-creation",groupId = "group-creation-consumer")
    public void listen(GroupCreateRequestDTO group) {
        groupMemberListener.onMessage(group);
    }

}
