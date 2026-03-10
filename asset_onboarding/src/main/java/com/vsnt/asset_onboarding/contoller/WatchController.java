package com.vsnt.asset_onboarding.contoller;

import com.vsnt.asset_onboarding.entities.Media;
import com.vsnt.asset_onboarding.entities.enums.AssetType;
import com.vsnt.asset_onboarding.entities.enums.MediaStatus;
import com.vsnt.asset_onboarding.exceptions.EntityNotFoundException;
import com.vsnt.asset_onboarding.services.*;
import com.vsnt.asset_onboarding.strategies.delivery.DeliverySecurityConfig;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/watch")
public class WatchController {
    private final MediaService mediaService;
    private final AuthorisationService authorisationService;

    private final DeliverySecurityConfig deliverySecurityConfig;
    private final WatchService watchService;

    public WatchController(MediaService mediaService, AuthorisationService authorisationService, DeliverySecurityConfig deliverySecurityConfig, WatchService watchService) {
        this.mediaService = mediaService;
        this.authorisationService = authorisationService;
         this.deliverySecurityConfig = deliverySecurityConfig;
        this.watchService = watchService;
    }
    @GetMapping("/{mediaId}")
    public ResponseEntity<?> watch(@PathVariable  UUID mediaId , @RequestHeader Map<String, String> headers , @RequestParam(
            defaultValue = "-1"
    ) long start , HttpServletResponse httpServletResponse)
    {
        Media media = mediaService.getMedia(mediaId);

        if(media == null || !(media.getStatus().equals(MediaStatus.READY) || media.getStatus().equals(MediaStatus.LIVE)))
        {
            throw new EntityNotFoundException("Media");
        }
        String userId = headers.get("X-USER-ID");
        String pullKey = headers.get("X-PULL-KEY");
        boolean allowed = authorisationService.authorise(media,userId,pullKey);
        if(!allowed)
        {
            throw new RuntimeException("Forbidden");
        }
        String content = watchService.watch(media , start);
        ResponseEntity<?> responseEntity = ResponseEntity.ok().body(content);
      deliverySecurityConfig.populateResponse(responseEntity, httpServletResponse,media,content);
      return responseEntity;
    }
    @GetMapping("/live/{mediaId}/{resolution}/playlist")
    public ResponseEntity<?> watchResolution(@PathVariable  UUID mediaId , @PathVariable  String resolution, @RequestParam(defaultValue = "-1") Long start , HttpServletResponse httpServletResponse)
    {
        Media media = mediaService.getMedia(mediaId);
        if(media== null)
        {
            throw new EntityNotFoundException("Media Not Found");
        }
        if(!media.getVideoAsset().getAssetType().equals(AssetType.LIVE_VIDEO))
        {
            throw new RuntimeException("Unsupported Media");
        }
        String content = watchService.watchLiveVariant(media,resolution,start);

        ResponseEntity<?> res = ResponseEntity.ok().body(content);
        deliverySecurityConfig.populateResponse(res, httpServletResponse,media,content);
        return res;
    }


}
