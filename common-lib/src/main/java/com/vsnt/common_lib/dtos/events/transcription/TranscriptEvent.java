package com.vsnt.common_lib.dtos.events.transcription;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.ToString;

public record TranscriptEvent(
        @JsonProperty("asset_id")
        String assetId,

        @JsonProperty("media_id")
        String mediaId,

        @JsonProperty("chunk_number")
        Integer chunkNumber,

        @JsonProperty("start_time")
        Double startTime,

        @JsonProperty("end_time")
        Double endTime,

        @JsonProperty("transcript")
        String transcript
) {}