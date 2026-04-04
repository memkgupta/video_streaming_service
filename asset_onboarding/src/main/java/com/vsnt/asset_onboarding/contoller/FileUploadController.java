package com.vsnt.asset_onboarding.contoller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vsnt.asset_onboarding.dtos.*;
import com.vsnt.asset_onboarding.exceptions.ForbiddenException;
import com.vsnt.asset_onboarding.exceptions.UnauthorisedException;
import com.vsnt.asset_onboarding.services.AuthorisationService;
import com.vsnt.asset_onboarding.services.UploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
/*End user controller*/
@Tag(
        name = "File Upload Endpoints",
        description = """
                Endpoints related to the file uploading of the static video , all of these endpoints are available for the 
                end user access so that the end user can directly upload the video to the service bypassing the platform
                and for secure access to the resources while calling the endpoint , user requires to attach the push key for
                that media given to them by the platform in the header by the name 'X-PUSH-KEY'
                """
)
@RestController
@RequestMapping("/v1/file")
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
        if(pushKey==null || pushKey.isEmpty())
        {
            throw new UnauthorisedException("Pause upload");
        }
        if(!authorisationService.canPush(String.valueOf(chunkUploadRequest.getAssetId()), pushKey))
        {
            throw new ForbiddenException("Push content to media");
        }
        String url = uploadService.uploadChunk(
                chunkUploadRequest.getUploadId(),
                chunkUploadRequest.getAssetId(),
                chunkUploadRequest.getPartNumber()


        );

        return ResponseEntity.ok(Map.of("url", url));
    }

    @Operation(
            summary = "Finalize upload",
            description = "Completes the multipart upload."
    )
    @PostMapping("/complete-upload")
    public ResponseEntity<Void> completeUpload(
            @RequestBody FinalizeUploadRequest finalizeUploadRequest,
            @Parameter(hidden = true) HttpServletRequest request,
           @Parameter(name = "Push key" , required = true, description = "Media push key given to user by the platform") @RequestHeader("X-PUSH-KEY") String pushKey
    ) {
        String userId = request.getHeader("X-USER-ID");
        if(pushKey==null || pushKey.isEmpty())
        {
            throw new UnauthorisedException("Pause upload");
        }
        if(!authorisationService.canPush(String.valueOf(finalizeUploadRequest.getAssetId()), pushKey))
        {
            throw new ForbiddenException("Finalize uploading");
        }

            uploadService.finishUpload(finalizeUploadRequest.getUploadId(),finalizeUploadRequest.getAssetId(),finalizeUploadRequest.getKey(),finalizeUploadRequest.getEtagMap(),userId);

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
            @Parameter(name = "Push key" , required = true,description = "Media push key given to user by the platform") @RequestHeader("X-PUSH-KEY") String pushKey
            ) {

        String userId = request.getHeader("X-USER-ID");
        if(pushKey==null || pushKey.isEmpty())
        {
            throw new UnauthorisedException("Pause upload");
        }
        if(!authorisationService.canPush(String.valueOf(uploadPauseToggleRequest.getAssetId()), pushKey))
        {
            throw new ForbiddenException("Pause Upload");
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
            @Parameter(name = "Push key" , required = true,description = "Media push key given to user by the platform") @RequestHeader("X-PUSH-KEY") String pushKey
            ) {

        String userId = request.getHeader("X-USER-ID");
        if(pushKey==null || pushKey.isEmpty())
        {
            throw new UnauthorisedException("Pause upload");
        }
        if(!authorisationService.canPush(String.valueOf(uploadPauseToggleRequest.getAssetId()), pushKey))
        {
            throw new UnauthorisedException("Resume Upload");
        }
        return uploadService.resumeUpload(
                uploadPauseToggleRequest.getAssetId(),
                userId
        );
    }
}
