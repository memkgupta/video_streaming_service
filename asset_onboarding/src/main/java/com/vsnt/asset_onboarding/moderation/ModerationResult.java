package com.vsnt.asset_onboarding.moderation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vsnt.asset_onboarding.dtos.ModerationStatus;
import lombok.Data;

import com.fasterxml.jackson.annotation.JsonValue;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ModerationResult {

    @JsonProperty("job_id")
    private String jobId;

    @JsonProperty("asset_id")
    private String assetId;

    @JsonProperty("content_type")
    private ContentType contentType;

    @JsonProperty("status")
    private ModerationStatus status;

    @JsonProperty("overall_score")
    private double overallScore;

    @JsonProperty("flags")
    private List<ModerationFlag> flags;

    @JsonProperty("chunk_results")
    private List<ChunkResult> chunkResults;

    @JsonProperty("chunks_total")
    private int chunksTotal;

    @JsonProperty("chunks_flagged")
    private int chunksFlagged;

    @JsonProperty("violation_count")
    private int violationCount;

    @JsonProperty("processing_ms")
    private long processingMs;

    @JsonProperty("metadata")
    private Map<String, Object> metadata;

    @JsonProperty("completed_at")
    private Instant completedAt;

    // ---------- Nested Classes ----------

    @Data
    public static class ModerationFlag {

        @JsonProperty("category")
        private ModerationCategory category;

        @JsonProperty("score")
        private double score;

        @JsonProperty("evidence")
        private String evidence;
    }

    @Data
    public static class ChunkResult {

        @JsonProperty("chunk_id")
        private int chunkId;

        @JsonProperty("start_time")
        private double startTime;

        @JsonProperty("end_time")
        private double endTime;

        @JsonProperty("nsfw_score")
        private double nsfwScore;

        @JsonProperty("violence_score")
        private double violenceScore;

        @JsonProperty("hate_score")
        private double hateScore;

        @JsonProperty("violation_count")
        private int violationCount;

        @JsonProperty("processing_time")
        private double processingTime;

        @JsonProperty("error")
        private String error;
    }
}


 enum ContentType {

    TEXT("text"),
    IMAGE("image"),
    VIDEO("video");

    private final String value;

    ContentType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}

 enum ModerationCategory {
     NSFW("nsfw"),
     HATE("hate"),
     VIOLENCE("violence"),
     SPAM("spam"),
     PII("pii");

     private final String value;

     ModerationCategory(String value) {
         this.value = value;
     }

     @JsonValue
     public String getValue() {
         return value;
     }

     @JsonCreator
     public static ModerationCategory fromValue(String value) {
         for (ModerationCategory category : ModerationCategory.values()) {
             if (category.value.equalsIgnoreCase(value)) {
                 return category;
             }
         }
         throw new IllegalArgumentException("Unknown ModerationCategory: " + value);
     }
}