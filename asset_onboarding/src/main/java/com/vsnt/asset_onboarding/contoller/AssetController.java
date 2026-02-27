package com.vsnt.asset_onboarding.contoller;

import com.vsnt.asset_onboarding.dtos.AssetDTO;
import com.vsnt.asset_onboarding.entities.Asset;
import com.vsnt.asset_onboarding.services.AssetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/assets")
@Tag(name = "Assets", description = "APIs for managing assets")
public class AssetController {

    private final AssetService assetService;

    @Operation(
            summary = "Get Asset by ID",
            description = "Fetches asset details by its ID and returns the asset DTO"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Asset found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AssetDTO.class))),
            @ApiResponse(responseCode = "404", description = "Asset not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<AssetDTO> getAssetById(
            @Parameter(description = "ID of the asset to be fetched", example = "1")
            @PathVariable("id") long id
    ) {
        Asset asset = assetService.getAssetById(id);
        AssetDTO dto = asset.toDTO();
        return ResponseEntity.ok(dto);
    }

}
