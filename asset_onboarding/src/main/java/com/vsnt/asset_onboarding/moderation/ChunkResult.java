package com.vsnt.asset_onboarding.moderation;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChunkResult {

    @JsonProperty("chunk_id")
    public int chunkId;

    @JsonProperty("start_time")
    public double startTime;

    @JsonProperty("end_time")
    public double endTime;

    @JsonProperty("nsfw_score")
    public double nsfwScore;

    @JsonProperty("violence_score")
    public double violenceScore;

    @JsonProperty("hate_score")
    public double hateScore;

    @JsonProperty("processing_time")
    public double processingTime;

    public String error; // nullable
}
