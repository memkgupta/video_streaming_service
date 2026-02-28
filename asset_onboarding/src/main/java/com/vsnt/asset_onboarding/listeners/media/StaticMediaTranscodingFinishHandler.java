package com.vsnt.asset_onboarding.listeners.media;

import com.vsnt.asset_onboarding.entities.Asset;
import com.vsnt.asset_onboarding.entities.Media;
import com.vsnt.asset_onboarding.entities.enums.MediaType;
import com.vsnt.asset_onboarding.services.AssetService;
import com.vsnt.asset_onboarding.workers.PlaylistGeneratorWorker;
import org.springframework.stereotype.Component;

@Component
public class StaticMediaTranscodingFinishHandler implements MediaFinishHandler{
    private final PlaylistGeneratorWorker playlistGeneratorWorker;
    private final AssetService assetService;
    public StaticMediaTranscodingFinishHandler(PlaylistGeneratorWorker playlistGeneratorWorker, AssetService assetService) {
        this.playlistGeneratorWorker = playlistGeneratorWorker;
        this.assetService = assetService;
    }

    @Override
    public void handle(Media media) {
        Asset asset =media.getVideoAsset();
        asset.setCdnURL(  playlistGeneratorWorker.generatePlaylist(media));
        assetService.save(asset);
    }

    @Override
    public MediaType supports() {
        return MediaType.STATIC;
    }
}
