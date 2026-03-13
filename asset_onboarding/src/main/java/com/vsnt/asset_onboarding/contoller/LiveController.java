package com.vsnt.asset_onboarding.contoller;

import com.vsnt.asset_onboarding.SecuredCDNService;
import com.vsnt.asset_onboarding.dtos.live.LiveStartResponseDTO;
import com.vsnt.asset_onboarding.entities.Asset;
import com.vsnt.asset_onboarding.entities.AssetAESKey;
import com.vsnt.asset_onboarding.entities.Media;
import com.vsnt.asset_onboarding.entities.enums.MediaStatus;
import com.vsnt.asset_onboarding.exceptions.EntityNotFoundException;
import com.vsnt.asset_onboarding.listeners.media.LiveMediaFinishHandler;
import com.vsnt.asset_onboarding.services.AssetService;
import com.vsnt.asset_onboarding.services.AuthorisationService;
import com.vsnt.asset_onboarding.services.KeyService;
import com.vsnt.asset_onboarding.services.MediaService;
import com.vsnt.asset_onboarding.strategies.asset.LiveVideoAssetCreation;
import com.vsnt.asset_onboarding.strategies.asset.LiveVideoAssetCreationRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.Base64;
import java.util.UUID;

@RestController
@RequestMapping("/live")
public class LiveController {
    private final MediaService mediaService;
    private final LiveVideoAssetCreation liveVideoAssetCreation;
    private final AssetService assetService;
    private final KeyService keyService;
    private final SecuredCDNService securedCDNService;
    private final LiveMediaFinishHandler finishHandler;
    private final AuthorisationService authorisationService;
    public LiveController(MediaService mediaService, LiveVideoAssetCreation liveVideoAssetCreation, AssetService assetService, KeyService keyService, SecuredCDNService securedCDNService, LiveMediaFinishHandler finishHandler, AuthorisationService authorisationService) {
        this.mediaService = mediaService;
        this.liveVideoAssetCreation = liveVideoAssetCreation;
        this.assetService = assetService;
        this.keyService = keyService;
        this.securedCDNService = securedCDNService;
        this.finishHandler = finishHandler;
        this.authorisationService = authorisationService;
    }
@PostMapping("/{mediaId}")
public ResponseEntity<?> startLive(@PathVariable UUID mediaId, @RequestBody LiveVideoAssetCreationRequestDTO  metadata,@RequestHeader("X-PUSH-KEY") String pushKey)
{
    Media media = mediaService.getMedia(mediaId);
    if(media == null)
    {
        throw new EntityNotFoundException("Media");
    }
    if(!authorisationService.canPush(media , pushKey))
    {
        throw new RuntimeException("Access denied");
    }
    Asset asset = assetService.createAsset(
            media , liveVideoAssetCreation,metadata
    );

   AssetAESKey assetKey = keyService.getKey(asset.getId().toString());
    byte[] key = securedCDNService.fetchSecure(assetKey.getKeyURL());
    media.setStatus(MediaStatus.LIVE);
    mediaService.save(media);
    LiveStartResponseDTO liveStartResponseDTO = LiveStartResponseDTO.builder()
            .encryptionKey(  Base64.getEncoder().encodeToString(key))
            .assetId(asset.getId().toString())
            .isModerationEnabled(media.isModerationEnabled())
            .build();
    return ResponseEntity.ok().body(liveStartResponseDTO);
}
@PutMapping("/end/{mediaId}")
    public ResponseEntity<?> endLive(@PathVariable UUID mediaId , @RequestHeader("X-PUSH-KEY") String pushKey)
{
    Media media = mediaService.getMedia(mediaId);
    if(media == null)
    {
        throw new EntityNotFoundException("Media");
    }
    if(!authorisationService.canPush(media , pushKey))
    {
        throw new RuntimeException("Access denied");
    }
    finishHandler.handle(media);
    return ResponseEntity.ok().build();
}

}
