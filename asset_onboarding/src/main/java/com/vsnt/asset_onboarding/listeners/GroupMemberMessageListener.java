package com.vsnt.asset_onboarding.listeners;

import com.vsnt.asset_onboarding.MessageListener;
import com.vsnt.asset_onboarding.dtos.media.request.GroupCreateRequestDTO;
import com.vsnt.asset_onboarding.dtos.media.request.GroupMemberCreateRequestDTO;
import com.vsnt.asset_onboarding.services.GroupMemberService;
import org.springframework.stereotype.Component;

@Component
public class GroupMemberMessageListener implements MessageListener<GroupMemberCreateRequestDTO> {
    private final GroupMemberService groupMemberService;

    public GroupMemberMessageListener(GroupMemberService groupMemberService) {
        this.groupMemberService = groupMemberService;
    }

    @Override
    public void onMessage(GroupMemberCreateRequestDTO message) {
    groupMemberService.createGroupMember(message);
    }
}
