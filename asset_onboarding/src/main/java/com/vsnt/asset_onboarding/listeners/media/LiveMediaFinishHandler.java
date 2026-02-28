package com.vsnt.asset_onboarding.listeners.media;

import com.vsnt.asset_onboarding.entities.Asset;
import com.vsnt.asset_onboarding.entities.Media;
import com.vsnt.asset_onboarding.entities.enums.AssetType;
import com.vsnt.asset_onboarding.entities.enums.MediaType;
import com.vsnt.asset_onboarding.services.AssetService;
import com.vsnt.asset_onboarding.workers.PlaylistGeneratorWorker;
import org.springframework.stereotype.Component;

@Component
public class LiveMediaFinishHandler implements MediaFinishHandler {
    private final AssetService assetService;
    private final PlaylistGeneratorWorker playlistGeneratorWorker;
    public LiveMediaFinishHandler(AssetService assetService, PlaylistGeneratorWorker playlistGeneratorWorker) {
        this.assetService = assetService;
        this.playlistGeneratorWorker = playlistGeneratorWorker;
    }

    @Override
    public void handle(Media media) {
        String playlist  = playlistGeneratorWorker.generatePlaylist(media);
        Asset asset =media.getVideoAsset();
        asset.setAssetType(AssetType.VIDEO);
        asset.setCdnURL(playlist);
        assetService.save(asset);
    }

    @Override
    public MediaType supports() {
        return MediaType.LIVE;
    }
}
