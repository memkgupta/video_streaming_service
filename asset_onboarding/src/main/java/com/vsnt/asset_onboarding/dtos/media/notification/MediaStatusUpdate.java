package com.vsnt.asset_onboarding.dtos.media.notification;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vsnt.asset_onboarding.entities.enums.MediaStatus;
import com.vsnt.asset_onboarding.entities.enums.MediaType;
import lombok.*;

import java.time.Instant;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MediaStatusUpdate {
    private MediaStatus mediaStatus;
    private String mediaId;
    private MediaType mediaType;
    private Map<String, Object> message;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant createdAt;
}