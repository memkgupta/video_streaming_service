package com.vsnt.asset_onboarding.dtos.media.response;

import java.sql.Timestamp;
import java.util.UUID;

public class KeyDTO {
    private String key;
    private Timestamp createdAt;
    private boolean active;
    private String userId;
    private UUID mediaId;
}
