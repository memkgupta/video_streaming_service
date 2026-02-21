package com.vsnt.asset_onboarding.contoller;

import com.vsnt.asset_onboarding.dtos.AssetDTO;
import com.vsnt.asset_onboarding.entities.Asset;
import com.vsnt.asset_onboarding.services.AssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/assets")
public class AssetController {
    private final AssetService assetService;

    @GetMapping("/{id}")
    public ResponseEntity<AssetDTO> getAssetById(@PathVariable("id") long id) {
        Asset asset = assetService.getAssetById(id);
        AssetDTO dto = asset.toDTO();
        return ResponseEntity.ok(dto);
    }

}
