package com.vsnt.asset_onboarding.contoller;

import com.vsnt.asset_onboarding.config.AuthenticateRequest;
import com.vsnt.asset_onboarding.dtos.*;
import com.vsnt.asset_onboarding.services.UploadService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")

public class FileUploadController {
    private final UploadService uploadService;
    private final AuthenticateRequest authenticateRequest;
    public FileUploadController(UploadService uploadService, AuthenticateRequest authenticateRequest) {
        this.uploadService = uploadService;
        this.authenticateRequest = authenticateRequest;
    }

    @PostMapping("/start-upload")
    public ResponseEntity<FileUploadStartResponse> startUpload(@RequestBody FileMetaData fileMetaData, HttpServletRequest request) {

        String userId = request.getAttribute("userId").toString();
        return ResponseEntity.ok(uploadService.startUpload(fileMetaData, userId));
    }
    @GetMapping("/upload-chunk")
    public String uploadChunk(@RequestBody ChunkUploadRequest chunkUploadRequest, HttpServletRequest request) {
        String userId = request.getAttribute("userId").toString();
        return uploadService.uploadChunk(chunkUploadRequest.getUploadId(),chunkUploadRequest.getAssetId(),chunkUploadRequest.getPartNumber(),chunkUploadRequest.getKey(), userId);
    }
    @PostMapping("/complete-upload")
    public Boolean completeUpload(@RequestBody FinalizeUploadRequest finalizeUploadRequest, HttpServletRequest request) {
        return uploadService.finishUpload(finalizeUploadRequest.getUploadId(),finalizeUploadRequest.getAssetId(),finalizeUploadRequest.getKey(),finalizeUploadRequest.getEtagMap());
    }
    @PostMapping("/pause-upload")
    public Boolean pauseUpload(@RequestBody UploadPauseToggleRequest uploadPauseToggleRequest, HttpServletRequest request) {
        String userId = request.getAttribute("userId").toString();
        return uploadService.pauseUpload(uploadPauseToggleRequest.getAssetId(),userId,uploadPauseToggleRequest.getEtagMap());
    }
    @PostMapping("/resume-upload")
    public Boolean resumeUpload(@RequestBody UploadPauseToggleRequest uploadPauseToggleRequest, HttpServletRequest request) {
        String userId = request.getAttribute("userId").toString();
        return uploadService.resumeUpload(uploadPauseToggleRequest.getAssetId(), userId);
    }
}
