package com.vsnt.asset_onboarding.contoller;

import com.vsnt.asset_onboarding.entities.Media;
import com.vsnt.asset_onboarding.entities.enums.AssetType;
import com.vsnt.asset_onboarding.entities.enums.MediaStatus;
import com.vsnt.asset_onboarding.exceptions.EntityNotFoundException;
import com.vsnt.asset_onboarding.services.*;
import com.vsnt.asset_onboarding.strategies.delivery.DeliverySecurityConfig;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.kafka.common.errors.InvalidRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@Tag(
        name = "Watch",
        description = "Endpoints for watching the media for end users , requires access token in form of bearer auth"
)
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
    @SecurityRequirement(name="bearerAuth")
    @Operation(
            summary = "Watch Media",
            description = "Get the content for the player to play the media"
    )
    @GetMapping("/{mediaId}")
    public ResponseEntity<?> watch(@PathVariable  UUID mediaId , @RequestHeader Map<String, String> headers , @Parameter(
            name = "start",
            description = "offset in milliseconds"
    ) @RequestParam(
            defaultValue = "-1"
    ) long start , HttpServletResponse httpServletResponse)
    {
        Media media = mediaService.getMedia(mediaId);
        if(media == null || !(media.getStatus().equals(MediaStatus.READY) || media.getStatus().equals(MediaStatus.LIVE)))
        {
            throw new EntityNotFoundException("Media");
        }
    if(!headers.containsKey("Authorization"))
    {
        throw new InvalidRequestException("Authorization");
    }
        String token = headers.get("Authorization");
    if(!token.startsWith("Bearer "))
    {
        throw new InvalidRequestException("Invalid Token");
    }
         token = token.substring(7);
        boolean allowed = authorisationService.canWatch(media,token);
        if(!allowed)
        {
            throw new RuntimeException("Forbidden");
        }
        String content = watchService.watch(media , start);
        ResponseEntity<?> responseEntity = ResponseEntity.ok().body(content);
      deliverySecurityConfig.populateResponse(responseEntity, httpServletResponse,media,content);

      return responseEntity;
    }
    @Operation(
            summary = "Watch resolution of live stream",
            description = "Endpoint for getting playlist for particular resolution of media "
    )
    @GetMapping("/live/{mediaId}/{resolution}/playlist")
    public ResponseEntity<?> watchResolution(@PathVariable  UUID mediaId , @PathVariable  String resolution, @Parameter(
            name = "start",
            description = "offset in milliseconds"
    ) @RequestParam(defaultValue = "-1") Long start , HttpServletResponse httpServletResponse ,  @RequestHeader(value = "Authorisation",defaultValue = "") String authHeader)
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

        if(!authHeader.startsWith("Bearer "))
        {
            throw new InvalidRequestException("Invalid Token");
        }
        String token  = authHeader.substring(7);
        boolean allowed = authorisationService.canWatch(media,token);

        if(!allowed)
        {
            throw new  RuntimeException("Forbidden");
        }
        String content = watchService.watchLiveVariant(media,resolution,start);

        ResponseEntity<?> res = ResponseEntity.ok().body(content);
        deliverySecurityConfig.populateResponse(res, httpServletResponse,media,content);
        return res;
    }


}
