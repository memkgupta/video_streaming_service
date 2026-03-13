package com.vsnt.asset_onboarding.repositories;

import com.vsnt.asset_onboarding.entities.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface MediaRepository extends JpaRepository<Media, UUID> , JpaSpecificationExecutor<Media> {
    Optional<Media> findByVideoAsset_Id(Long assetId);
}
