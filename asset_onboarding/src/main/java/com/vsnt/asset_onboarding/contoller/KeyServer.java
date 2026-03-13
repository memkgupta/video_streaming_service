package com.vsnt.asset_onboarding.contoller;

import com.vsnt.asset_onboarding.CDNService;
import com.vsnt.asset_onboarding.entities.AssetAESKey;
import com.vsnt.asset_onboarding.services.AuthorisationService;
import com.vsnt.asset_onboarding.services.KeyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
/*End user controller to be used by the player*/
@RestController
@RequestMapping("/v1/key")
public class KeyServer {
    private final CDNService cdnService;
    private final KeyService keyService;
    private final AuthorisationService authorisationService;
    public KeyServer(CDNService cdnService, KeyService keyService, AuthorisationService authorisationService) {
        this.cdnService = cdnService;
        this.keyService = keyService;
        this.authorisationService = authorisationService;
    }

    @GetMapping("/{assetId}")
    public ResponseEntity<byte[]>
        getKey(@PathVariable("assetId") String assetID , @RequestHeader("Authorisation") String authToken) throws Exception {

        if(!authToken.startsWith("Bearer "))
        {
            throw new Exception("Invalid Token");
        }
        authToken = authToken.substring(7);
    if(!authorisationService.canWatch(assetID,authToken))
    {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }
        AssetAESKey assetKey = keyService.getKey(assetID);
       byte[] key =cdnService.fetch(assetKey.getKeyURL());
       return  ResponseEntity.ok()
               .contentType(MediaType.APPLICATION_OCTET_STREAM)
               .body(key);
    }
}
