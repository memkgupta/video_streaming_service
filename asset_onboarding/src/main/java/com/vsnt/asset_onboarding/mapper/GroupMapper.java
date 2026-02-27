package com.vsnt.asset_onboarding.mapper;

import com.vsnt.asset_onboarding.dtos.media.response.GroupDTO;
import com.vsnt.asset_onboarding.entities.Group;
import org.springframework.stereotype.Component;

@Component
public class GroupMapper {
    public GroupDTO toGroupDTO(Group group) {
        if(group == null) return null;
        return GroupDTO.builder()
                .id(group.getId())
                .name(group.getName())
                .active(group.isActive())
                .createdAt(group.getCreatedAt())
                .updatedAt(group.getUpdatedAt())

                .build();
    }
}
