package com.vsnt.videos_service.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RedisSegmentDTO {
    private long segmentId;
    private String streamKey;
    private String url;
}
