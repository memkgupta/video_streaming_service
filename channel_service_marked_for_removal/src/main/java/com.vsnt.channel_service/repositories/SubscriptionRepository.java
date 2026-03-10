package com.vsnt.channel_service.repositories;

import com.vsnt.channel_service.entities.Channel;
import com.vsnt.channel_service.entities.Subscription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<Subscription,Long> {
    int countByChannel(Channel channel);
    Page<Subscription> getSubscriptionByUserId(String userId, Pageable pageable);
}
