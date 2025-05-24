package com.vsnt.channel_service.entities;

import com.vsnt.channel_service.payload.channel.ChannelPayload;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Entity
@Data
public class Channel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String name;
    private String description;
    private String profile;
    private String handle;
    private String banner;
    @ElementCollection
    private List<Links> links;
    private String userId;

    public ChannelPayload toDTO()
    {
        ChannelPayload channelPayload = new ChannelPayload();
        channelPayload.setId(id);
        channelPayload.setName(name);
        channelPayload.setDescription(description);
        channelPayload.setProfile(profile);
        channelPayload.setHandle(handle);
        channelPayload.setBanner(banner);
        channelPayload.setLinks(links);
        channelPayload.setUserId(userId);
        return channelPayload;
    }
}
