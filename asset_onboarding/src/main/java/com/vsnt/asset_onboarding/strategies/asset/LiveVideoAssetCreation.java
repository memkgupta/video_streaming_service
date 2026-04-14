package com.vsnt.asset_onboarding.strategies.asset;

import com.vsnt.asset_onboarding.AssetCreationStrategy;
import com.vsnt.asset_onboarding.entities.Asset;
import com.vsnt.asset_onboarding.entities.Media;
import com.vsnt.asset_onboarding.entities.enums.AssetType;
import com.vsnt.asset_onboarding.repositories.AssetRepository;
import com.vsnt.asset_onboarding.repositories.MediaRepository;
import com.vsnt.asset_onboarding.services.AssetService;
import com.vsnt.asset_onboarding.services.KeyService;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;

@Component
public class LiveVideoAssetCreation extends AssetCreationStrategy<LiveVideoAssetCreationRequestDTO> {

   private final AssetRepository assetRepository;
    private final MediaRepository mediaRepository;

    protected LiveVideoAssetCreation(KeyService keyService, AssetRepository assetRepository, MediaRepository mediaRepository) {
        super(keyService,true,mediaRepository);

        this.assetRepository = assetRepository;
        this.mediaRepository = mediaRepository;
    }

    @Override
    protected Asset helper(Media media, LiveVideoAssetCreationRequestDTO metadata) {
        Asset asset = new Asset();
        asset.setMediaId(media.getId());
        asset.setChunksUploaded(0);
        asset.setAssetType(AssetType.LIVE_VIDEO);
        asset.setStartTime(Timestamp.from(Instant.now()));
        return assetRepository.save(asset);
    }
}
