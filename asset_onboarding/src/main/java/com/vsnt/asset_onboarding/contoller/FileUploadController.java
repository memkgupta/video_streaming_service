package com.vsnt.asset_onboarding.contoller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vsnt.asset_onboarding.config.AuthenticateRequest;
import com.vsnt.asset_onboarding.config.TranscodingJobMessageProducer;
import com.vsnt.asset_onboarding.dtos.*;
import com.vsnt.asset_onboarding.services.UploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/")
@Tag(name = "File Upload APIs", description = "Handle file chunk uploads and finalization")
public class FileUploadController {

    private final UploadService uploadService;
    private final TranscodingJobMessageProducer transcodingJobMessageProducer;

    public FileUploadController(UploadService uploadService, AuthenticateRequest authenticateRequest, TranscodingJobMessageProducer transcodingJobMessageProducer) {
        this.uploadService = uploadService;
        this.transcodingJobMessageProducer = transcodingJobMessageProducer;
    }

    @Operation(
            summary = "Start a new file upload",
            description = "Initiates upload session and returns upload ID and asset ID."
    )
    @PostMapping("/start-upload")
    public ResponseEntity<FileUploadStartResponse> startUpload(
            @RequestBody FileMetaData fileMetaData,
            @Parameter(hidden = true) HttpServletRequest request) {
        String userId = request.getHeader("X-USER-ID");
        return ResponseEntity.ok(uploadService.startUpload(fileMetaData, userId));
    }

    @Operation(
            summary = "Upload a file chunk",
            description = "Uploads a specific part of a file using upload ID and part number."
    )
    @PostMapping("/upload-chunk")
    public ResponseEntity<Map<String, String>> uploadChunk(
            @RequestBody ChunkUploadRequest chunkUploadRequest,
            @Parameter(hidden = true) HttpServletRequest request) {
        String userId = request.getHeader("X-USER-ID");

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
            @Parameter(hidden = true) HttpServletRequest request) throws JsonProcessingException {

        String userId = request.getHeader("X-USER-ID");
        uploadService.finishUpload(
                finalizeUploadRequest.getUploadId(),
                finalizeUploadRequest.getAssetId(),
                finalizeUploadRequest.getKey(),
                finalizeUploadRequest.getEtagMap(),
                userId
        );
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Pause file upload",
            description = "Temporarily pauses an ongoing file upload session."
    )
    @PostMapping("/pause-upload")
    public Boolean pauseUpload(
            @RequestBody UploadPauseToggleRequest uploadPauseToggleRequest,
            @Parameter(hidden = true) HttpServletRequest request) {
        String userId = request.getHeader("X-USER-ID");
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
            @Parameter(hidden = true) HttpServletRequest request) {
        String userId = request.getHeader("X-USER-ID");
        return uploadService.resumeUpload(
                uploadPauseToggleRequest.getAssetId(),
                userId
        );
    }
}