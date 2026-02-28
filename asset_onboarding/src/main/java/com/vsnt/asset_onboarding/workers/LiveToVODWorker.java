package com.vsnt.asset_onboarding.workers;

import com.vsnt.asset_onboarding.entities.Asset;
import com.vsnt.asset_onboarding.entities.Media;
import com.vsnt.asset_onboarding.entities.enums.AssetType;
import com.vsnt.asset_onboarding.repositories.AssetRepository;
import org.springframework.stereotype.Component;

@Component
public class LiveToVODWorker {
private final PlaylistGeneratorWorker playlistGeneratorWorker;
private final AssetRepository assetRepository;
    public LiveToVODWorker(PlaylistGeneratorWorker playlistGeneratorWorker, AssetRepository assetRepository) {
        this.playlistGeneratorWorker = playlistGeneratorWorker;
        this.assetRepository = assetRepository;
    }

    public void convert(Media media) {


}
}
