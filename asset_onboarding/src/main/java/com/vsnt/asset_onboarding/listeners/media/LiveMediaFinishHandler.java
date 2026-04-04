package com.vsnt.asset_onboarding.listeners.media;

import com.vsnt.asset_onboarding.entities.Asset;
import com.vsnt.asset_onboarding.entities.Media;
import com.vsnt.asset_onboarding.entities.enums.AssetType;
import com.vsnt.asset_onboarding.entities.enums.MediaType;
import com.vsnt.asset_onboarding.repositories.MediaRepository;
import com.vsnt.asset_onboarding.services.AssetService;
import com.vsnt.asset_onboarding.services.MediaService;
import com.vsnt.asset_onboarding.workers.PlaylistGeneratorWorker;
import org.springframework.stereotype.Component;

@Component
public class LiveMediaFinishHandler implements MediaFinishHandler {
    private final AssetService assetService;
    private final PlaylistGeneratorWorker playlistGeneratorWorker;
    private final MediaService mediaService;

    public LiveMediaFinishHandler(AssetService assetService, PlaylistGeneratorWorker playlistGeneratorWorker, MediaRepository mediaRepository, MediaService mediaService) {
        this.assetService = assetService;
        this.playlistGeneratorWorker = playlistGeneratorWorker;
        this.mediaService = mediaService;
    }

    @Override
    public void handle(Media media) {
        String playlist  = playlistGeneratorWorker.generatePlaylist(media);
        Asset asset =media.getVideoAsset();
        asset.setAssetType(AssetType.VIDEO);
        asset.setCdnURL(playlist);
        media.setMediaType(MediaType.STATIC);
        assetService.save(asset);
        mediaService.save(media);
    }

    @Override
    public MediaType supports() {
        return MediaType.LIVE;
    }
}
