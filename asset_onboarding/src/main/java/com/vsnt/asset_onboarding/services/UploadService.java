package com.vsnt.asset_onboarding.services;

import com.vsnt.asset_onboarding.dtos.FileMetaData;
import com.vsnt.asset_onboarding.dtos.FileUploadStartResponse;
import com.vsnt.asset_onboarding.entities.Upload;
import com.vsnt.asset_onboarding.entities.enums.UploadStatus;

import com.vsnt.asset_onboarding.repositories.UploadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Map;
import java.util.UUID;

@Service

public class UploadService {
    private final S3Service s3Service;
    private final UploadRepository uploadRepository;
    private final AssetService assetService;

    public UploadService(S3Service s3Service, UploadRepository uploadRepository, AssetService assetService) {
        this.s3Service = s3Service;
        this.uploadRepository = uploadRepository;
        this.assetService = assetService;
    }

    public FileUploadStartResponse startUpload(FileMetaData obj,String userId)
    {
        String fileName = obj.getFileName();
        String fileUploadId = UUID.randomUUID().toString();
        String key = "uploads/"+fileName+"/"+fileUploadId;
        String uploadId = s3Service.startMultiPartUpload(key);
        Upload upload = new Upload();
        upload.setFileUploadId(fileUploadId);
        upload.setFileName(fileName);
        upload.setChunksUploaded(0);
        upload.setUploadStatus(UploadStatus.INITIATED);
        upload.setUploadId(uploadId);
        upload.setStartTime(new Timestamp(System.currentTimeMillis()));
        upload.setUserId(userId);
        upload.setKey(key);
        upload.setFileSize(obj.getFileSize());
        upload.setFileType(obj.getFileType());
        uploadRepository.save(upload);
      FileUploadStartResponse res = new FileUploadStartResponse();
      res.setKey(key);
      res.setAssetId(upload.getId().toString());
      res.setUploadId(uploadId);
return res;
    }
    public String uploadChunk(String uploadId,Long assetId,int partNumber,String key,String userId)
    {   Upload upload = assetService.getAssetById(assetId);
        if(upload==null || !upload.getUserId().equals(userId)){
            throw new RuntimeException("Bad request , upload doesn't exist");
        }
        if(!upload.getUploadStatus().equals(UploadStatus.INITIATED) || !upload.getUploadStatus().equals(UploadStatus.UPLOADING)){
            throw new RuntimeException("Bad request");
        }
        return s3Service.getPreSignedURLForMultipartUploadChunk(uploadId,partNumber,key);
    }
    public boolean finishUpload(String uploadId, Long assetId,String key, Map<Integer,String> etagMap)
    {
        Upload upload = assetService.getAssetById(assetId);
        if(upload==null){
            throw new RuntimeException("Bad request , upload doesn't exist");
        }
       s3Service.completeMultipartUpload(uploadId,etagMap,key);
        upload.setUploadStatus(UploadStatus.COMPLETED);
        upload.setEndTime(new Timestamp(System.currentTimeMillis()));
        upload.setChunksUploaded(etagMap.size());

        uploadRepository.save(upload);return true;
    }
    public boolean pauseUpload(Long assetId,String userId,Map<Integer,String> etagMap)
    {
        try{
            Upload upload = assetService.getAssetById(assetId);
            if(upload==null || !upload.getUserId().equals(userId))
            {
                throw new RuntimeException("Bad request , upload doesn't exist");
            }
            if(!upload.getUploadStatus().equals(UploadStatus.UPLOADING)){
                throw new RuntimeException("Bad request");
            }
            upload.setUploadStatus(UploadStatus.PAUSED);
           if(upload.getEtagMap()!=null){
               upload.getEtagMap().putAll(etagMap);
           }
           else{
               upload.setEtagMap(etagMap);
           }
            upload.setChunksUploaded(upload.getChunksUploaded()+etagMap.size());
            uploadRepository.save(upload);
            return true;
        }
        catch(Exception e){
            return false;
        }

    }
    public boolean resumeUpload(Long assetId,String userId)
    {
        try{
            Upload upload = assetService.getAssetById(assetId);
            if(upload==null || !upload.getUserId().equals(userId))
            {
                throw new RuntimeException("Bad request , upload doesn't exist");
            }
            if(!upload.getUploadStatus().equals(UploadStatus.PAUSED)){
                throw new RuntimeException("Bad request");
            }
            upload.setUploadStatus(UploadStatus.UPLOADING);


            uploadRepository.save(upload);
            return true;
        }
        catch(Exception e){
            return false;
        }
    }
}
