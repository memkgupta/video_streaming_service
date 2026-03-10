package com.vsnt.videos_service.repositories;

import com.vsnt.videos_service.entities.LiveStream;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LiveStreamRepository extends JpaRepository<
        LiveStream , UUID
        > {
    Optional<LiveStream> findByStreamKey(String streamKey);
}
