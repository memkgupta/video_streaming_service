package com.vsnt.channel_service.payload.channel;

import com.vsnt.channel_service.entities.Links;
import lombok.Data;

import java.util.List;

@Data
public class ChannelPayload {
    private String id;
    private String name;
    private String description;
    private List<Links> links;
    private String userId;
    private String banner;
    private String handle;
    private String profile;
}
