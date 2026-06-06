package com.vsnt.common_lib.dtos.events.transcription;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record TranscriptSegment(
        Double start,
        Double end,
        String text
) {
}
