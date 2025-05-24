package com.vsnt.channel_service.services;

import com.vsnt.channel_service.entities.Channel;
import com.vsnt.channel_service.entities.Links;
import com.vsnt.channel_service.exceptions.APIException;
import com.vsnt.channel_service.exceptions.ChannelNotFoundException;
import com.vsnt.channel_service.exceptions.InternalServerError;
import com.vsnt.channel_service.payload.channel.ChannelPayload;
import com.vsnt.channel_service.repositories.ChannelRepository;
import jakarta.ws.rs.core.Link;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChannelService {
    private final ChannelRepository channelRepository;
    public Channel findById(String id ) {
        return channelRepository.findById(id).orElse(null);
    }
    public Channel createChannel(ChannelPayload channelPayload) {
        Channel channel = new Channel();
        channel.setName(channelPayload.getName());
        channel.setDescription(channelPayload.getDescription());
        channel.setLinks(channelPayload.getLinks());
        channel.setHandle(channelPayload.getHandle());
        channel.setProfile(channelPayload.getProfile());
        channel.setUserId(channelPayload.getUserId());

        return channelRepository.save(channel);
    }
    public  Channel updateChannel(ChannelPayload channelPayload,String channelId)
    {
        Channel channel = findById(channelId);
        if(channel == null)
        {
            throw new RuntimeException("Channel not found");
        }
        if(channelPayload.getName() != null && !channelPayload.getName().isEmpty() && !channelPayload.getName().equals(channel.getName()))
        {
            channel.setName(channelPayload.getName());
        }
        if(channelPayload.getBanner() != null && !channelPayload.getBanner().isEmpty() && !channelPayload.getBanner().equals(channel.getName()))
        {
            channel.setBanner(channelPayload.getBanner());

        }
        if(channelPayload.getDescription() != null && !channelPayload.getDescription().isEmpty())
        {
            channel.setDescription(channelPayload.getDescription());
        }
        if(channelPayload.getProfile() != null && !channelPayload.getProfile().isEmpty())
        {
            channel.setProfile(channelPayload.getProfile());
        }
        if(channelPayload.getLinks()!=null)
        {
          for(int i = 0;i<channelPayload.getLinks().size();i++)
          {
              Links nl = channelPayload.getLinks().get(i);
              Links old = null;
              if(i<channel.getLinks().size())
              {
                  old = channel.getLinks().get(i);
              }
              if(old == null || old.getTitle()!=nl.getTitle() || old.getUrl()!=nl.getUrl())
              {
                old = new Links(nl);
              }
              channel.getLinks().add(old);
          }
        }
        return channelRepository.save(channel);

    }
    public Channel findByUserId(String userId)
    {
        try{
            Specification<Channel> specification = (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("userId"), userId);
            List<Channel> channel = channelRepository.findAll(specification);
            if(channel.isEmpty())
            {
                throw new ChannelNotFoundException("Channel not found");
            }
            return channel.get(0);
        }
        catch(Exception e)
        {
            if(e instanceof APIException)
            {
                throw  e;
            }
            e.printStackTrace();
            throw new InternalServerError(e.getLocalizedMessage());
        }
    }
    public Channel findByHandle(String handle) {
        return channelRepository.findByHandle(handle).orElse(null);
    }
}
