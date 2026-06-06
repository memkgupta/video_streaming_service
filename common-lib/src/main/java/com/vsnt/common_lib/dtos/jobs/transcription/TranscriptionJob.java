package com.vsnt.common_lib.dtos.jobs.transcription;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TranscriptionJob {
    private String assetId;
    private String mediaId;
    private String chunkUrl;
    private long chunkNumber;
    private double startTime;
    private double endTime;
}