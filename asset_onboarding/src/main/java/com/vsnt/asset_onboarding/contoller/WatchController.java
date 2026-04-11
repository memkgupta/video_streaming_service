package com.vsnt.asset_onboarding.contoller;

import com.vsnt.asset_onboarding.entities.Media;
import com.vsnt.asset_onboarding.entities.enums.AssetType;
import com.vsnt.asset_onboarding.entities.enums.MediaStatus;
import com.vsnt.asset_onboarding.exceptions.EntityNotFoundException;
import com.vsnt.asset_onboarding.exceptions.ForbiddenException;
import com.vsnt.asset_onboarding.exceptions.InvalidArgumentException;
import com.vsnt.asset_onboarding.exceptions.UnauthorisedException;
import com.vsnt.asset_onboarding.services.*;
import com.vsnt.asset_onboarding.strategies.delivery.DeliverySecurityConfig;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.kafka.common.errors.InvalidRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Tag(
        name = "Watch",
        description = "Endpoints for watching the media for end users , requires access token in form of bearer auth"
)
@RestController
@RequestMapping("/v1/watch")
public class WatchController {
    private final MediaService mediaService;
    private final DeliverySecurityConfig deliverySecurityConfig;
    private final WatchService watchService;

    public WatchController(MediaService mediaService, DeliverySecurityConfig deliverySecurityConfig, WatchService watchService) {
        this.mediaService = mediaService;
         this.deliverySecurityConfig = deliverySecurityConfig;
        this.watchService = watchService;
    }
    @SecurityRequirement(name="bearerAuth")
    @Operation(
            summary = "Watch Media",
            description = "Get the content for the player to play the media",
            parameters = {
                    @Parameter(
                            name = "X-ACCESS-TOKE",
                            in = ParameterIn.HEADER
                    )
            }
    )
    @GetMapping("/{mediaId}")
    public ResponseEntity<?> watch(@PathVariable  UUID mediaId ,@Parameter(
            name = "start",
            description = "offset in milliseconds"
    ) @RequestParam(
            defaultValue = "-1"
    ) long start ,@RequestHeader("X-ASSET-ID") String assetId, HttpServletResponse httpServletResponse)
    {
        Media media = mediaService.getMedia(mediaId);
        isMediaAvailableToWatch(mediaId, media);
        if(assetId == null || assetId.isEmpty())
    {
        throw new UnauthorisedException("Watch");
    }

        boolean allowed = media.getVideoAsset().getId().equals(Long.parseLong(assetId));
        if(!allowed)
        {
            throw new ForbiddenException("Watch media");
        }
        String content = watchService.watch(media , start);


        return deliverySecurityConfig.populateResponse( httpServletResponse,media,content);
    }

    @Operation(
            summary = "Watch resolution of live stream",
            description = "Endpoint for getting playlist for particular resolution of media ",
            parameters = {
                    @Parameter(
                            name = "X-ACCESS-TOKEN",
                            in = ParameterIn.HEADER
                    )
            }
    )
    @GetMapping("/live/{mediaId}/{resolution}/playlist.m3u8")
    public ResponseEntity<?> watchResolution(@PathVariable  UUID mediaId , @PathVariable  String resolution, @Parameter(
            name = "start",
            description = "offset in milliseconds"
    ) @RequestParam(defaultValue = "-1") Long start , HttpServletResponse httpServletResponse , @RequestHeader("X-ASSET-ID") String assetId )
    {
        Media media = mediaService.getMedia(mediaId);
        isMediaAvailableToWatch(mediaId, media);
        if(!media.getVideoAsset().getAssetType().equals(AssetType.LIVE_VIDEO))
        {
            throw new InvalidArgumentException("resolution",resolution,"valid resolution");
        }
        boolean allowed = media.getVideoAsset().getId().equals(Long.parseLong(assetId));
        if(!allowed)
        {
            throw new ForbiddenException("Watch");
        }
        String content = watchService.watchLiveVariant(media,resolution,start);
        return  deliverySecurityConfig.populateResponse( httpServletResponse,media,content);

    }

    private static void isMediaAvailableToWatch(UUID mediaId, Media media) {
        if(media == null || !media.isActive() || !(media.getStatus().equals(MediaStatus.READY) || media.getStatus().equals(MediaStatus.LIVE)))
        {
            throw new EntityNotFoundException("Media", mediaId.toString());
        }
    }


}
