package com.vsnt.asset_onboarding.strategies.asset;

import com.vsnt.asset_onboarding.AssetCreationStrategy;
import com.vsnt.asset_onboarding.entities.Asset;
import com.vsnt.asset_onboarding.entities.Media;
import com.vsnt.asset_onboarding.entities.enums.AssetType;
import com.vsnt.asset_onboarding.repositories.AssetRepository;
import com.vsnt.asset_onboarding.services.AssetService;
import com.vsnt.asset_onboarding.services.KeyService;

public class LiveVideoAssetCreation extends AssetCreationStrategy<LiveVideoAssetCreationRequestDTO> {
   private final KeyService keyService;
   private final AssetRepository assetRepository;
    protected LiveVideoAssetCreation(KeyService keyService, KeyService keyService1, AssetRepository assetRepository) {
        super(keyService,true);
        this.keyService = keyService1;
        this.assetRepository = assetRepository;
    }

    @Override
    public Asset helper(Media media, LiveVideoAssetCreationRequestDTO metadata) {
        Asset asset = new Asset();
        asset.setMediaId(media.getId());
        asset.setChunksUploaded(0);
        asset.setAssetType(AssetType.LIVE_VIDEO);
        return assetRepository.save(asset);
    }
}
