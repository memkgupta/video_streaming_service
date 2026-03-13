package com.vsnt.asset_onboarding.contoller;

import com.vsnt.asset_onboarding.dtos.FileMetaData;
import com.vsnt.asset_onboarding.dtos.FileUploadStartResponse;
import com.vsnt.asset_onboarding.dtos.PageResponseDTO;
import com.vsnt.asset_onboarding.dtos.media.request.MediaCreateRequestDTO;
import com.vsnt.asset_onboarding.dtos.media.response.MediaDTO;
import com.vsnt.asset_onboarding.entities.Asset;
import com.vsnt.asset_onboarding.entities.Media;
import com.vsnt.asset_onboarding.exceptions.EntityNotFoundException;
import com.vsnt.asset_onboarding.mapper.MediaMapper;
import com.vsnt.asset_onboarding.services.AssetService;
import com.vsnt.asset_onboarding.services.AuthorisationService;
import com.vsnt.asset_onboarding.services.MediaService;
import com.vsnt.asset_onboarding.services.S3Service;
import com.vsnt.asset_onboarding.strategies.asset.StaticVideoAssetCreation;
import com.vsnt.asset_onboarding.strategies.asset.ThumbnailAssetCreation;
import com.vsnt.asset_onboarding.strategies.delivery.DeliverySecurityConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/v1/media")
@Tag(
        name = "Media endpoints",
        description = """
                Media API endpoints availaible for SDKs to manage media and generating access token ,\s
                all these routes are protected and requires api key info in form of headers\s
                X-ACCESS-KEY,
                X-ACCESS-SECRET
               \s"""
)
public class MediaController {
    private final MediaService mediaService;
    private final MediaMapper mediaMapper;
    private final AssetService assetService;
    private final ThumbnailAssetCreation  thumbnailAssetCreation;
    private final StaticVideoAssetCreation staticVideoAssetCreation;
    private final DeliverySecurityConfig deliverySecurityConfig;
    private final S3Service s3Service;
    private final AuthorisationService authorisationService;
    public MediaController(MediaService mediaService, MediaMapper mediaMapper, AssetService assetService, ThumbnailAssetCreation thumbnailAssetCreation, StaticVideoAssetCreation staticVideoAssetCreation, DeliverySecurityConfig deliverySecurityConfig, S3Service s3Service, AuthorisationService authorisationService) {
        this.mediaService = mediaService;
        this.mediaMapper = mediaMapper;
        this.assetService = assetService;
        this.thumbnailAssetCreation = thumbnailAssetCreation;
        this.staticVideoAssetCreation = staticVideoAssetCreation;
        this.deliverySecurityConfig = deliverySecurityConfig;
        this.s3Service = s3Service;
        this.authorisationService = authorisationService;
    }
    @Operation(
            summary = "Create media",
            description = "Create the media ",
            parameters = {
                    @Parameter(name = "X-ACCESS-SECRET" , required = true,in = ParameterIn.HEADER),
                    @Parameter(name = "X-ACCESS-KEY" ,  required = true,in = ParameterIn.HEADER),
            }
    )
    @PostMapping
    public ResponseEntity<MediaDTO> createMedia(@RequestBody MediaCreateRequestDTO request)
    {
        Media media = mediaService.createMedia(request);
        MediaDTO dto = mediaMapper.toMediaDTO(media);
        return ResponseEntity.ok(dto);
    }
    @Operation(
            summary = "Delete media",
            description = "Delete the media ",
            parameters = {
                    @Parameter(name = "X-ACCESS-SECRET" , required = true,in = ParameterIn.HEADER),
                    @Parameter(name = "X-ACCESS-KEY" ,  required = true,in = ParameterIn.HEADER),
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMedia(@PathVariable UUID id)
    {
        mediaService.deleteMedia(id);
        return ResponseEntity.noContent().build();
    }
    @Operation(
            summary = "Fetch media",
            description = "Fetch media by the id",
            parameters = {
                    @Parameter(name = "X-ACCESS-SECRET",required = true , in = ParameterIn.HEADER),
                    @Parameter(name = "X-ACCESS-KEY" , required = true, in = ParameterIn.HEADER),
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<MediaDTO> getMedia(@PathVariable UUID id)
    {
        Media media = mediaService.getMedia(id);
        MediaDTO dto = mediaMapper.toMediaDTO(media);
        return ResponseEntity.ok(dto);
    }
    @Operation(
            summary = "Fetch all media",
            description = "Fetch all media in paginated way  ",
            parameters = {
                    @Parameter(name = "X-ACCESS-SECRET",required = true , in = ParameterIn.HEADER),
                    @Parameter(name = "X-ACCESS-KEY" , required = true, in = ParameterIn.HEADER),
                    @Parameter(name="page",required = true,in=ParameterIn.DEFAULT),
                    @Parameter(name = "limit",required = false ,in =  ParameterIn.DEFAULT),
            }
    )
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
    @Operation(
            summary = "Update Thumbnail",
            description = "Update thumbnail of the media and gives the presigned url for uploading the thumbnail by the end user or platform directly",
            parameters = {
                    @Parameter(name = "X-ACCESS-SECRET" , in = ParameterIn.HEADER),
                    @Parameter(name = "X-ACCESS-KEY" ,  in = ParameterIn.HEADER),
            }
    )
    @PutMapping("/{id}/thumbnail")
    public ResponseEntity<?> updateMediaThumbnail(@PathVariable UUID id, @RequestBody FileMetaData metaData)
    {
    Media media = mediaService.getMedia(id);
    if(media == null)
    {
        throw new EntityNotFoundException("Media");
    }
    Asset asset = assetService.createAsset(media ,thumbnailAssetCreation,metaData);
    String preSignedURL = s3Service.startSingleUpload(asset.getKey(),asset.getFileType());
    return ResponseEntity.ok(Map.of("preSignedURL",preSignedURL));
    }
    @Operation(
            summary = "Start Video Uplaod",
            description = "Start Video upload for static media returns metadata along with the push key",
            parameters = {
                    @Parameter(name = "X-ACCESS-SECRET" , in = ParameterIn.HEADER),
                    @Parameter(name = "X-ACCESS-KEY" ,  in = ParameterIn.HEADER),
            }
    )
    @PutMapping("/{id}/video")
    public ResponseEntity<FileUploadStartResponse> startUploadVideo(
            @PathVariable UUID id, @RequestBody FileMetaData metaData
    )
    {
        Media media = mediaService.getMedia(id);
        if(media == null)
        {
            throw new EntityNotFoundException("Media");
        }
        Asset asset = assetService.createAsset(media ,staticVideoAssetCreation,metaData);
        FileUploadStartResponse res = new  FileUploadStartResponse();
        res.setKey(asset.getKey());
        res.setPushKey(media.getPushKey().getKey());
        res.setUploadId(asset.getUploadId());
        res.setAssetId(asset.getId().toString());
        return ResponseEntity.ok(res);
    }
    @Operation(
            summary = "Generate Access Tokens",
            description = "Generate the access tokens to be given to end user in order to access the protected media , " +
                    "returns the access token and refresh token",
            parameters = {
                    @Parameter(name = "X-ACCESS-SECRET" , in = ParameterIn.HEADER),
                    @Parameter(name = "X-ACCESS-KEY" ,  in = ParameterIn.HEADER),
            }
    )
    @GetMapping("/{id}/generate-tokens")
    public ResponseEntity<?> generateTokens(@PathVariable UUID id , @RequestParam("userId") String userId)
    {
        Media media = mediaService.getMedia(id);
        if(media == null)
        {
            throw new EntityNotFoundException("Media");
        }
        String[] tokens = deliverySecurityConfig.generateTokens(userId,media.getVideoAsset().getId().toString());
        return ResponseEntity.ok(Map.of("access_token",tokens[0],"refresh_token",tokens[1]));
    }


}
