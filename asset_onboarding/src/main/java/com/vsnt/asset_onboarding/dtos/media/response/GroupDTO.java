package com.vsnt.asset_onboarding.dtos.media.response;

import lombok.Builder;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@Builder
public class GroupDTO {
    private UUID id;
    private String name;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private boolean active;
}
