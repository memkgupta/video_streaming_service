package com.vsnt.asset_onboarding.contoller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vsnt.asset_onboarding.config.AuthenticateRequest;
import com.vsnt.asset_onboarding.config.TranscodingJobMessageProducer;
import com.vsnt.asset_onboarding.dtos.*;
import com.vsnt.asset_onboarding.services.UploadService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/")

public class FileUploadController {
    private final UploadService uploadService;

    private final TranscodingJobMessageProducer transcodingJobMessageProducer;

    public FileUploadController(UploadService uploadService, AuthenticateRequest authenticateRequest, TranscodingJobMessageProducer transcodingJobMessageProducer) {
        this.uploadService = uploadService;

        this.transcodingJobMessageProducer = transcodingJobMessageProducer;
    }

    @PostMapping("/start-upload")
    public ResponseEntity<FileUploadStartResponse> startUpload(@RequestBody FileMetaData fileMetaData, HttpServletRequest request) {

        String userId = request.getHeader("X-USER-ID");
        return ResponseEntity.ok(uploadService.startUpload(fileMetaData, userId));
    }
    @PostMapping("/upload-chunk")
    public ResponseEntity<Map<String, String>> uploadChunk(@RequestBody ChunkUploadRequest chunkUploadRequest, HttpServletRequest request) {

        String userId = request.getHeader("X-USER-ID");

        String url = uploadService.uploadChunk(chunkUploadRequest.getUploadId(),chunkUploadRequest.getAssetId(),chunkUploadRequest.getPartNumber(),chunkUploadRequest.getKey(), userId);

        return ResponseEntity.ok(   Map.of("url", url));
    }
    @PostMapping("/complete-upload")
    public ResponseEntity completeUpload(@RequestBody FinalizeUploadRequest finalizeUploadRequest, HttpServletRequest request) throws JsonProcessingException {
        String userId = request.getHeader("X-USER-ID");
         uploadService.finishUpload(finalizeUploadRequest.getUploadId(),finalizeUploadRequest.getAssetId(),finalizeUploadRequest.getKey(),finalizeUploadRequest.getEtagMap(),userId);
    return ResponseEntity.noContent().build();
    }
    @PostMapping("/pause-upload")
    public Boolean pauseUpload(@RequestBody UploadPauseToggleRequest uploadPauseToggleRequest, HttpServletRequest request) {
        String userId = request.getHeader("X-USER-ID");
        return uploadService.pauseUpload(uploadPauseToggleRequest.getAssetId(),userId,uploadPauseToggleRequest.getEtagMap());
    }
    @PostMapping("/resume-upload")
    public Boolean resumeUpload(@RequestBody UploadPauseToggleRequest uploadPauseToggleRequest, HttpServletRequest request) {
        String userId = request.getHeader("X-USER-ID");
        return uploadService.resumeUpload(uploadPauseToggleRequest.getAssetId(), userId);
    }

}
