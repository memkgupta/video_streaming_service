package com.q4labs.notification.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.q4labs.notification.enums.MediaStatus;
import com.q4labs.notification.enums.MediaType;
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
