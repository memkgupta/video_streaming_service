package com.vsnt.asset_onboarding.repositories;

import com.vsnt.asset_onboarding.entities.MediaPushKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MediaPushKeyRepository extends JpaRepository<MediaPushKey , UUID> {
    Optional<MediaPushKey> findByKey(String key);
}
