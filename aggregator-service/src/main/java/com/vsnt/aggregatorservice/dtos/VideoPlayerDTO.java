package com.vsnt.aggregatorservice.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VideoPlayerDTO extends VideoDTO {
    private String url;
    private long likes;
    private long duration;
    private long totalComments;
    private ChannelDTO channelDetails;
}
