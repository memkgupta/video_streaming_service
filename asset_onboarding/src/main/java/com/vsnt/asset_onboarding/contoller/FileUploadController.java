package com.vsnt.asset_onboarding.contoller;

import com.vsnt.asset_onboarding.dtos.ChunkUploadRequest;
import com.vsnt.asset_onboarding.dtos.FileMetaData;
import com.vsnt.asset_onboarding.dtos.FileUploadStartResponse;
import com.vsnt.asset_onboarding.dtos.FinalizeUploadRequest;
import com.vsnt.asset_onboarding.services.UploadService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")

public class FileUploadController {
    private final UploadService uploadService;

    public FileUploadController(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    @PostMapping("/start-upload")
    public ResponseEntity<FileUploadStartResponse> startUpload(@RequestBody FileMetaData fileMetaData, HttpServletRequest request) {
        return ResponseEntity.ok(uploadService.startUpload(fileMetaData));
    }
    @GetMapping("/upload-chunk")
    public String uploadChunk(@RequestBody ChunkUploadRequest chunkUploadRequest, HttpServletRequest request) {
        return uploadService.uploadChunk(chunkUploadRequest.getUploadId(),chunkUploadRequest.getPartNumber(),chunkUploadRequest.getKey());
    }
    @PostMapping("/complete-upload")
    public Boolean completeUpload(@RequestBody FinalizeUploadRequest finalizeUploadRequest, HttpServletRequest request) {
        return uploadService.finishUpload(finalizeUploadRequest.getUploadId(),finalizeUploadRequest.getKey(),finalizeUploadRequest.getEtagMap());
    }
}
