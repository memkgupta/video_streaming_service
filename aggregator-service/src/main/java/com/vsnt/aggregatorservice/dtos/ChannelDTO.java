package com.vsnt.aggregatorservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChannelDTO {
    private String id;
    private String name;
    private String description;
    private List<Links> links;
    private String userId;
    private String banner;
    private String handle;
    private String profile;
    private Long subscribersCount;
}
