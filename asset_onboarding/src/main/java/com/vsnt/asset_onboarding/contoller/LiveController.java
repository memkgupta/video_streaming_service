package com.vsnt.asset_onboarding.contoller;

import com.vsnt.asset_onboarding.KeyCDNService;
import com.vsnt.asset_onboarding.dtos.live.LiveStartResponseDTO;
import com.vsnt.asset_onboarding.entities.Asset;
import com.vsnt.asset_onboarding.entities.AssetAESKey;
import com.vsnt.asset_onboarding.entities.Media;
import com.vsnt.asset_onboarding.entities.enums.MediaStatus;
import com.vsnt.asset_onboarding.exceptions.EntityNotFoundException;
import com.vsnt.asset_onboarding.services.AssetService;
import com.vsnt.asset_onboarding.services.KeyService;
import com.vsnt.asset_onboarding.services.MediaService;
import com.vsnt.asset_onboarding.strategies.asset.LiveVideoAssetCreation;
import com.vsnt.asset_onboarding.strategies.asset.LiveVideoAssetCreationRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class LiveController {
    private final MediaService mediaService;
    private final LiveVideoAssetCreation liveVideoAssetCreation;
    private final AssetService assetService;
    private final KeyService keyService;
    private final KeyCDNService keyCDNService;
    public LiveController(MediaService mediaService, LiveVideoAssetCreation liveVideoAssetCreation, AssetService assetService, KeyService keyService, KeyCDNService keyCDNService) {
        this.mediaService = mediaService;
        this.liveVideoAssetCreation = liveVideoAssetCreation;
        this.assetService = assetService;
        this.keyService = keyService;
        this.keyCDNService = keyCDNService;
    }
@PostMapping("/{mediaId}")
public ResponseEntity<?> startLive(@PathVariable UUID mediaId, @RequestBody LiveVideoAssetCreationRequestDTO  metadata)
{
    Media media = mediaService.getMedia(mediaId);
    if(media == null)
    {
        throw new EntityNotFoundException("Media");
    }
    Asset asset = assetService.createAsset(
            media , liveVideoAssetCreation,metadata
    );

   AssetAESKey assetKey = keyService.getKey(asset.getId().toString());
    byte[] key = keyCDNService.fetchSecure(assetKey.getKeyURL());
    media.setStatus(MediaStatus.LIVE);
    mediaService.save(media);
    LiveStartResponseDTO liveStartResponseDTO = LiveStartResponseDTO.builder()
            .encryptionKey(key)
            .isModerationEnabled(media.isModerationEnabled())
            .build();
    return ResponseEntity.ok().body(liveStartResponseDTO);
}
}
