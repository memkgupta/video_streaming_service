package com.vsnt.asset_onboarding.listeners.media;

import com.vsnt.asset_onboarding.entities.Asset;
import com.vsnt.asset_onboarding.entities.Media;
import com.vsnt.asset_onboarding.entities.enums.MediaStatus;
import com.vsnt.asset_onboarding.entities.enums.MediaType;
import com.vsnt.asset_onboarding.entities.enums.UploadStatus;
import com.vsnt.asset_onboarding.repositories.MediaRepository;
import com.vsnt.asset_onboarding.services.AssetService;
import com.vsnt.asset_onboarding.workers.PlaylistGeneratorWorker;
import org.springframework.stereotype.Component;

@Component
public class StaticMediaTranscodingFinishHandler implements MediaFinishHandler{
    private final PlaylistGeneratorWorker playlistGeneratorWorker;
    private final AssetService assetService;
    private final MediaRepository mediaRepository;

    public StaticMediaTranscodingFinishHandler(PlaylistGeneratorWorker playlistGeneratorWorker, AssetService assetService, MediaRepository mediaRepository) {
        this.playlistGeneratorWorker = playlistGeneratorWorker;
        this.assetService = assetService;
        this.mediaRepository = mediaRepository;
    }

    @Override
    public void handle(Media media) {
        Asset asset =media.getVideoAsset();
        media.setStatus(MediaStatus.READY);
        asset.setUploadStatus(UploadStatus.COMPLETED);
        asset.setCdnURL(  playlistGeneratorWorker.generatePlaylist(media));
        assetService.save(asset);
        mediaRepository.save(media);
    }

    @Override
    public MediaType supports() {
        return MediaType.STATIC;
    }
}
