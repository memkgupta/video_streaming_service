package com.vsnt.videos_service.dtos;

import lombok.Data;

@Data
public class StreamSegmentUpdateDTO {
    private String streamKey;
    private Long segmentId;
    private String url;
    private Long start;
    private Long end;
    private Long duration;

}
