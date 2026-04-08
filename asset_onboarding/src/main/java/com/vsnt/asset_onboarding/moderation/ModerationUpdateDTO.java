package com.vsnt.asset_onboarding.moderation;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vsnt.asset_onboarding.dtos.ModerationStatus;
import lombok.Data;

@Data
public class ModerationUpdateDTO {

//    @JsonProperty("moderation_status")
    private ModerationStatus moderationStatus;

//    @JsonProperty("moderation_result")
    private ModerationResult moderationResult;

//    @JsonProperty("asset_id")
    private String assetId;

//    @JsonProperty("job_id")
    private String jobId;

//    @JsonProperty("violation_count")
    private long violationCount;
}