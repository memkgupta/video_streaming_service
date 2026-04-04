package com.vsnt.asset_onboarding.services;


import com.vsnt.asset_onboarding.dtos.*;
import com.vsnt.asset_onboarding.entities.Asset;
import com.vsnt.asset_onboarding.entities.enums.UploadStatus;

import com.vsnt.asset_onboarding.exceptions.BadRequestException;
import com.vsnt.asset_onboarding.exceptions.EntityNotFoundException;
import com.vsnt.asset_onboarding.exceptions.InvalidStateException;
import com.vsnt.asset_onboarding.listeners.AssetUploadFinishListener;
import com.vsnt.asset_onboarding.repositories.AssetRepository;

import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Map;
import java.util.UUID;

@Service

public class UploadService {
    private final S3Service s3Service;
    private final AssetRepository assetRepository;
    private final AssetService assetService;

    private final AssetUploadFinishListener uploadFinishListener;
    public UploadService(S3Service s3Service, AssetRepository assetRepository, AssetService assetService,  AssetUploadFinishListener uploadFinishListener) {
        this.s3Service = s3Service;
        this.assetRepository = assetRepository;
        this.assetService = assetService;

        this.uploadFinishListener = uploadFinishListener;
    }

    public FileUploadStartResponse startUpload(FileMetaData obj,String userId)
    {
        String fileName = obj.getFileName();
        String fileUploadId = UUID.randomUUID().toString();
        String key = "uploads/"+fileName.split("\\.")[0]+"/"+fileUploadId+".mp4";
        String uploadId = s3Service.startMultiPartUpload(key);
        Asset upload = new Asset();
        upload.setFileUploadId(fileUploadId);
        upload.setFileName(fileName);
        upload.setChunksUploaded(0);
        upload.setUploadStatus(UploadStatus.INITIATED);
        upload.setUploadId(uploadId);
        upload.setStartTime(new Timestamp(System.currentTimeMillis()));
        upload.setMediaId(UUID.fromString(obj.getMediaId()));
        upload.setKey(key);
        upload.setFileSize(obj.getFileSize());
        upload.setFileType(obj.getFileType());
        assetRepository.save(upload);


      FileUploadStartResponse res = new FileUploadStartResponse();
      res.setKey(key);
      res.setAssetId(upload.getId().toString());
      res.setUploadId(uploadId);
return res;
    }
    public String uploadChunk(String uploadId,Long assetId,int partNumber)
    {

        Asset upload = assetService.getAssetById(assetId);
        if(upload==null){

            throw new EntityNotFoundException("Asset" , assetId.toString());
        }

        if(!upload.getUploadStatus().equals(UploadStatus.INITIATED) ){
            throw new InvalidStateException("Not started" , "upload chunk");
        }
        return s3Service.getPreSignedURLForMultipartUploadChunk(uploadId,partNumber,upload.getKey());
    }
    public void finishUpload(String uploadId, Long assetId, String key, Map<Integer,String> etagMap, String userId) {
        Asset upload = assetService.getAssetById(assetId);
        if(upload==null){
            throw new EntityNotFoundException("Asset", assetId.toString());
        }
        upload.setUploadStatus(UploadStatus.COMPLETED);
        upload.setEndTime(new Timestamp(System.currentTimeMillis()));
        upload.setChunksUploaded(etagMap.size());
        assetRepository.save(upload);
        s3Service.completeMultipartUpload(uploadId,etagMap,key);
        uploadFinishListener.listen(upload);




    }
    public boolean pauseUpload(Long assetId,String userId,Map<Integer,String> etagMap)
    {

           Asset upload = assetService.getAssetById(assetId);
        if(upload==null){
            throw new EntityNotFoundException("Asset", assetId.toString());
        }
            if(!upload.getUploadStatus().equals(UploadStatus.UPLOADING)){
                throw new BadRequestException("Bad request");
            }
            upload.setUploadStatus(UploadStatus.PAUSED);
           if(upload.getEtagMap()!=null){
               upload.getEtagMap().putAll(etagMap);
           }
           else{
               upload.setEtagMap(etagMap);
           }
            upload.setChunksUploaded(upload.getChunksUploaded()+etagMap.size());
            assetRepository.save(upload);
            return true;



    }
    public boolean resumeUpload(Long assetId,String userId)
    {

            Asset upload = assetService.getAssetById(assetId);
            if(upload==null)
            {
                throw new EntityNotFoundException("Asset", assetId.toString());
            }
            if(!upload.getUploadStatus().equals(UploadStatus.PAUSED)){
                throw new BadRequestException("Bad request");
            }
            upload.setUploadStatus(UploadStatus.UPLOADING);


            assetRepository.save(upload);
            return true;
        }

    }

