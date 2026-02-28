package com.vsnt.asset_onboarding.dtos.media.request;

import lombok.Data;

@Data
public class GroupMemberCreateRequestDTO {
    private String memberUserId;
    private String groupId;

}
