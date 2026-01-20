package com.vsnt.asset_onboarding.dtos;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.Map;
@Data
public class ModerationResult {
    private String moderationResultKey;
    private ModerationStatus status;
    private double confidenceScore;
    private List<ModerationFlag> flags;
    private Map<String,Object> metadata;
    private String videoId;
}
