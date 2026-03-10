package com.vsnt.asset_onboarding.contoller;

import com.vsnt.asset_onboarding.CDNService;
import com.vsnt.asset_onboarding.entities.AssetAESKey;
import com.vsnt.asset_onboarding.services.KeyService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/key")
public class KeyServer {
    private final CDNService cdnService;
    private final KeyService keyService;
    public KeyServer(CDNService cdnService, KeyService keyService) {
        this.cdnService = cdnService;
        this.keyService = keyService;
    }

    @GetMapping("/{assetId}")
    public ResponseEntity<byte[]>
        getKey(@PathVariable("assetId") String assetID) throws Exception {
        //todo add security check also
        AssetAESKey assetKey = keyService.getKey(assetID);
       byte[] key =cdnService.fetch(assetKey.getKeyURL());
       return  ResponseEntity.ok()
               .contentType(MediaType.APPLICATION_OCTET_STREAM)
               .body(key);
    }
}
