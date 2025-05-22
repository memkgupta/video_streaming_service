package com.vsnt.channel_service.services;

import com.vsnt.channel_service.entities.Channel;
import com.vsnt.channel_service.entities.Subscription;
import com.vsnt.channel_service.repositories.SubscriptionRepository;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final ChannelService channelService;

    public Page<Subscription> getSubscriptions(String userId,int page,int size) {
        Page<Subscription> subscriptions = subscriptionRepository.getSubscriptionByUserId(
                userId, PageRequest.of(page,size, Sort.by("createdAt").descending())

        );
        return subscriptions;
    }
    public Subscription subscribe(String channelId,String userId)
    {
        Channel channel = channelService.findById(channelId);
        if(channel==null)
        {
            throw new NotFoundException(channelId);
        }
        Subscription subscription = new Subscription();
        subscription.setChannel(channel);
        subscription.setUserId(userId);
        subscriptionRepository.save(subscription);
        return subscription;
    }
    public void unSubscribe(Long subscriptionId,String userId)
    {
        Subscription subscription = findById(subscriptionId);
        if(subscription==null || !subscription.getUserId().equals(userId))
        {
            throw new NotFoundException(String.valueOf(subscriptionId));
        }
        subscriptionRepository.delete(subscription);
    }
    public Subscription findById(Long subscriptionId)
    {
        return subscriptionRepository.findById(subscriptionId).orElse(null);
    }
}
