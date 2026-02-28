package com.vsnt.asset_onboarding.listeners;

import com.vsnt.asset_onboarding.MessageListener;
import com.vsnt.asset_onboarding.dtos.media.request.GroupCreateRequestDTO;
import com.vsnt.asset_onboarding.entities.Group;
import com.vsnt.asset_onboarding.services.GroupService;
import org.springframework.stereotype.Component;

@Component
public class GroupListener implements MessageListener<GroupCreateRequestDTO> {
    private final GroupService groupService;

    public GroupListener(GroupService groupService) {
        this.groupService = groupService;
    }

    @Override
    public void onMessage(GroupCreateRequestDTO message) {
        groupService.createGroup(message);
    }
}
