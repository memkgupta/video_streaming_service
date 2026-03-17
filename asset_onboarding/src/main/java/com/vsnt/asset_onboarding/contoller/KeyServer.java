package com.vsnt.asset_onboarding.contoller;

import com.vsnt.asset_onboarding.CDNService;
import com.vsnt.asset_onboarding.entities.Asset;
import com.vsnt.asset_onboarding.entities.AssetAESKey;
import com.vsnt.asset_onboarding.exceptions.EntityNotFoundException;
import com.vsnt.asset_onboarding.exceptions.ForbiddenException;
import com.vsnt.asset_onboarding.exceptions.UnauthorisedException;
import com.vsnt.asset_onboarding.services.AssetService;
import com.vsnt.asset_onboarding.services.AuthorisationService;
import com.vsnt.asset_onboarding.services.KeyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
/*End user controller to be used by the player*/
@Tag(
        name="Key Server",
        description = """
                Endpoint related to fetching key for decrypting the segments in the index file
                for the player , the player has to attach the header Authorisation with bearer 
                token , and the token will be the Access token again given by the platform to the 
                end user to access a particular media 
                """
)
@RestController
@RequestMapping("/v1/key")
public class KeyServer {
    private final CDNService cdnService;
    private final KeyService keyService;

    private final AssetService assetService;

    public KeyServer(CDNService cdnService, KeyService keyService,  AssetService assetService) {
        this.cdnService = cdnService;
        this.keyService = keyService;

        this.assetService = assetService;
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{assetId}")
    @Operation(
            summary = "Fetch the key"
    )
    public ResponseEntity<byte[]>
        getKey(@PathVariable("assetId") String assetID , @RequestHeader("X-ASSET-ID") String assetHeader) throws Exception {
        Asset asset = assetService.getAssetById(Long.parseLong(assetID));
        if(asset==null){
            throw new EntityNotFoundException("Asset" , assetID);
        }
        if(assetHeader==null || assetHeader.isEmpty() )
        {
            throw new UnauthorisedException("Fetch key");
        }
        boolean allowed =  assetHeader.equals(assetID);
        if(!allowed){
            throw new ForbiddenException("Fetch key");
        }
        AssetAESKey assetKey = keyService.getKey(assetID);
       byte[] key =cdnService.fetch(assetKey.getKeyURL());
       return  ResponseEntity.ok()
               .contentType(MediaType.APPLICATION_OCTET_STREAM)
               .body(key);
    }
}
