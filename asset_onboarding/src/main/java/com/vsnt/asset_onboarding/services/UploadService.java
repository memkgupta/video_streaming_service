package com.vsnt.asset_onboarding.services;

import com.vsnt.asset_onboarding.dtos.FileMetaData;
import com.vsnt.asset_onboarding.dtos.FileUploadStartResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service

public class UploadService {
    private final S3Service s3Service;

    public UploadService(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    public FileUploadStartResponse startUpload(FileMetaData obj)
    {
        String fileName = obj.getFileName();
        String fileUploadId = UUID.randomUUID().toString();
        String uploadId = s3Service.startMultiPartUpload("uploads/"+fileName+"/"+fileUploadId);
        return new FileUploadStartResponse(uploadId, "uploads/"+fileName+"/"+fileUploadId);
    }
    public String uploadChunk(String uploadId,int partNumber,String key)
    {
        return s3Service.getPreSignedURLForMultipartUploadChunk(uploadId,partNumber,key);
    }
    public boolean finishUpload(String uploadId, String key, Map<Integer,String> etagMap)
    {
        return s3Service.completeMultipartUpload(uploadId,etagMap,key);
    }
}
