package com.vsnt.asset_onboarding.repositories;

import com.vsnt.asset_onboarding.entities.MediaAccessToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MediaAccessTokenRepository extends JpaRepository<MediaAccessToken, UUID> {
    Optional<MediaAccessToken> findByRefreshToken(String token);
}
