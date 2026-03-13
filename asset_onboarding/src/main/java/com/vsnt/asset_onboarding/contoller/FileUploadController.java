package com.vsnt.asset_onboarding.contoller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vsnt.asset_onboarding.dtos.*;
import com.vsnt.asset_onboarding.services.AuthorisationService;
import com.vsnt.asset_onboarding.services.UploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
/*End user controller*/
@RestController
@RequestMapping("/")
public class FileUploadController {
    private final UploadService uploadService;
    private final AuthorisationService authorisationService;
    public FileUploadController(UploadService uploadService, AuthorisationService authorisationService) {
        this.uploadService = uploadService;

        this.authorisationService = authorisationService;
    }
    @PostMapping("/upload-chunk")
    public ResponseEntity<Map<String, String>> uploadChunk(@RequestBody ChunkUploadRequest chunkUploadRequest, HttpServletRequest request, @RequestHeader("X-PUSH-KEY") String pushKey) {



        String userId = request.getHeader("X-USER-ID");
        if(!authorisationService.canPush(String.valueOf(chunkUploadRequest.getAssetId()), pushKey))
        {
            throw new RuntimeException("Not Authorised");
        }
        String url = uploadService.uploadChunk(
                chunkUploadRequest.getUploadId(),
                chunkUploadRequest.getAssetId(),
                chunkUploadRequest.getPartNumber(),
                chunkUploadRequest.getKey(),
                userId
        );

        return ResponseEntity.ok(Map.of("url", url));
    }

    @Operation(
            summary = "Finalize upload",
            description = "Completes the multipart upload and triggers transcoding job."
    )
    @PostMapping("/complete-upload")
    public ResponseEntity<Void> completeUpload(
            @RequestBody FinalizeUploadRequest finalizeUploadRequest,
            @Parameter(hidden = true) HttpServletRequest request,
            @RequestHeader("X-PUSH-KEY") String pushKey
    ) throws JsonProcessingException {


        String userId = request.getHeader("X-USER-ID");
        if(!authorisationService.canPush(String.valueOf(finalizeUploadRequest.getAssetId()), pushKey))
        {
            throw new RuntimeException("Not Authorised");
        }
        try {
            uploadService.finishUpload(finalizeUploadRequest.getUploadId(),finalizeUploadRequest.getAssetId(),finalizeUploadRequest.getKey(),finalizeUploadRequest.getEtagMap(),userId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Pause file upload",
            description = "Temporarily pauses an ongoing file upload session."
    )
    @PostMapping("/pause-upload")
    public Boolean pauseUpload(
            @RequestBody UploadPauseToggleRequest uploadPauseToggleRequest,
            @Parameter(hidden = true) HttpServletRequest request,
            @RequestHeader("X-PUSH-KEY") String pushKey
            ) {

        String userId = request.getHeader("X-USER-ID");
        if(!authorisationService.canPush(String.valueOf(uploadPauseToggleRequest.getAssetId()), pushKey))
        {
            throw new RuntimeException("Not Authorised");
        }
        return uploadService.pauseUpload(
                uploadPauseToggleRequest.getAssetId(),
                userId,
                uploadPauseToggleRequest.getEtagMap()
        );
    }

    @Operation(
            summary = "Resume file upload",
            description = "Resumes a previously paused file upload session."
    )
    @PostMapping("/resume-upload")
    public Boolean resumeUpload(
            @RequestBody UploadPauseToggleRequest uploadPauseToggleRequest,
            @Parameter(hidden = true) HttpServletRequest request,
            @RequestHeader("X-PUSH-KEY") String pushKey
            ) {

        String userId = request.getHeader("X-USER-ID");
        if(!authorisationService.canPush(String.valueOf(uploadPauseToggleRequest.getAssetId()), pushKey))
        {
            throw new RuntimeException("Not Authorised");
        }
        return uploadService.resumeUpload(
                uploadPauseToggleRequest.getAssetId(),
                userId
        );
    }


}
