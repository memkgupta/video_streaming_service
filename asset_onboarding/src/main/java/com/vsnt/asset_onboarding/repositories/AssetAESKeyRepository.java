package com.vsnt.asset_onboarding.repositories;

import com.vsnt.asset_onboarding.entities.AssetAESKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AssetAESKeyRepository extends JpaRepository<AssetAESKey, UUID> {
    Optional<AssetAESKey> findByAssetID(String assetId);
}
