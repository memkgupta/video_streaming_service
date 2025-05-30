package com.vsnt.asset_onboarding.repositories;

import com.vsnt.asset_onboarding.entities.Asset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssetRepository extends JpaRepository<Asset, Long> {
    public List<Asset> findAllByUserId(String userId);
    public Asset findById(long id);
    public Asset findByVideoId(String videoId);
}
