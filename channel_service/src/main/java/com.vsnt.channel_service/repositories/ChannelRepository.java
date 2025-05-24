package com.vsnt.channel_service.repositories;

import com.vsnt.channel_service.entities.Channel;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChannelRepository extends JpaRepository<Channel,String> {
    public Optional<Channel> findById(String id);
    Optional<Channel> findByHandle(String handle);
    List<Channel> findAll(Specification<Channel> spec);

}
