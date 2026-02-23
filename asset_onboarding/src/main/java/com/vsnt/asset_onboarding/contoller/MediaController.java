package com.vsnt.asset_onboarding.contoller;

import com.vsnt.asset_onboarding.dtos.FileMetaData;
import com.vsnt.asset_onboarding.dtos.PageResponseDTO;
import com.vsnt.asset_onboarding.dtos.media.request.MediaCreateRequestDTO;
import com.vsnt.asset_onboarding.dtos.media.response.MediaDTO;
import com.vsnt.asset_onboarding.entities.Asset;
import com.vsnt.asset_onboarding.entities.Media;
import com.vsnt.asset_onboarding.exceptions.EntityNotFoundException;
import com.vsnt.asset_onboarding.mapper.MediaMapper;
import com.vsnt.asset_onboarding.services.AssetService;
import com.vsnt.asset_onboarding.services.MediaService;
import com.vsnt.asset_onboarding.strategies.asset.StaticVideoAssetCreation;
import com.vsnt.asset_onboarding.strategies.asset.ThumbnailAssetCreation;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/media")
public class MediaController {
    private final MediaService mediaService;
    private final MediaMapper mediaMapper;
    private final AssetService assetService;
    private final ThumbnailAssetCreation  thumbnailAssetCreation;
    private final StaticVideoAssetCreation staticVideoAssetCreation;
    public MediaController(MediaService mediaService, MediaMapper mediaMapper, AssetService assetService, ThumbnailAssetCreation thumbnailAssetCreation, StaticVideoAssetCreation staticVideoAssetCreation) {
        this.mediaService = mediaService;
        this.mediaMapper = mediaMapper;
        this.assetService = assetService;
        this.thumbnailAssetCreation = thumbnailAssetCreation;
        this.staticVideoAssetCreation = staticVideoAssetCreation;
    }
    @PostMapping
    public ResponseEntity<MediaDTO> createMedia(@RequestBody MediaCreateRequestDTO request)
    {
        Media media = mediaService.createMedia(request);
        MediaDTO dto = mediaMapper.toMediaDTO(media);
        return ResponseEntity.ok(dto);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMedia(@PathVariable UUID id)
    {
        mediaService.deleteMedia(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/{id}")
    public ResponseEntity<MediaDTO> getMedia(@PathVariable UUID id)
    {
        Media media = mediaService.getMedia(id);
        MediaDTO dto = mediaMapper.toMediaDTO(media);
        return ResponseEntity.ok(dto);
    }
    @GetMapping
    public ResponseEntity<PageResponseDTO<MediaDTO>>
    getAll(@RequestParam HashMap<String ,String> params )
    {
        //todo generate spec from params
        Specification<Media> spec = Specification.allOf();
        int page = 1;
        int limit = 10;
        if (params.get("page") != null) {
         page = Integer.parseInt(params.get("page"));
        }
        if (params.get("limit") != null) {
            limit = Integer.parseInt(params.get("limit"));
        }
        Page<Media> resPage = mediaService.getAllMedia(
                spec , page , limit

        );
        List<MediaDTO> content =
                resPage.getContent().stream().map(mediaMapper::toMediaDTO).toList();
        PageResponseDTO<MediaDTO> pageResponseDTO =
                PageResponseDTO.<MediaDTO>builder()
                        .total(resPage.getTotalElements())
                        .hasPrevious(resPage.hasPrevious())
                        .hasNext(resPage.hasNext())
                        .data(content)
                        .build();
        return ResponseEntity.ok(pageResponseDTO);

    }
    @PutMapping("/{id}/thumbnail")
    public ResponseEntity<?> updateMediaThumbnail(@PathVariable UUID id, @RequestBody FileMetaData metaData)
    {
    Media media = mediaService.getMedia(id);
    if(media == null)
    {
        throw new EntityNotFoundException("Media");
    }
    Asset asset = assetService.createAsset(media ,thumbnailAssetCreation,metaData);
    return ResponseEntity.ok(asset.getCdnURL());
    }
    @PutMapping("/{id}/video")
    public ResponseEntity<?> startUploadVideo(
            @PathVariable UUID id, @RequestBody FileMetaData metaData
    )
    {
        Media media = mediaService.getMedia(id);
        if(media == null)
        {
            throw new EntityNotFoundException("Media");
        }
        Asset asset = assetService.createAsset(media ,staticVideoAssetCreation,metaData);
        return ResponseEntity.ok().build();
    }


}
