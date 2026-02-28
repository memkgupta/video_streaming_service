package com.vsnt.asset_onboarding.repositories;

import com.vsnt.asset_onboarding.entities.MediaPullKey;
import com.vsnt.asset_onboarding.entities.MediaPushKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MediaPullKeyRepository extends JpaRepository<MediaPullKey, UUID> {
    Optional<MediaPullKey> findByKey(String key);
}
