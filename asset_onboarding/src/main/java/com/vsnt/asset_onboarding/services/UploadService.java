package com.vsnt.asset_onboarding.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vsnt.asset_onboarding.config.KafkaProducer;
import com.vsnt.asset_onboarding.config.TranscodingJobMessageProducer;
import com.vsnt.asset_onboarding.dtos.FileMetaData;
import com.vsnt.asset_onboarding.dtos.FileUploadStartResponse;
import com.vsnt.asset_onboarding.dtos.TranscodingJob;
import com.vsnt.asset_onboarding.dtos.UpdateRequestDTO;
import com.vsnt.asset_onboarding.entities.Asset;
import com.vsnt.asset_onboarding.entities.enums.UploadStatus;

import com.vsnt.asset_onboarding.repositories.AssetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service

public class UploadService {
    private final S3Service s3Service;
    private final AssetRepository assetRepository;
    private final AssetService assetService;
    private final KafkaProducer kafkaProducer;
    private final TranscodingJobMessageProducer jobProducer;
    public UploadService(S3Service s3Service, AssetRepository assetRepository, AssetService assetService, KafkaProducer kafkaProducer, TranscodingJobMessageProducer messageProducer) {
        this.s3Service = s3Service;
        this.assetRepository = assetRepository;
        this.assetService = assetService;
        this.kafkaProducer = kafkaProducer;
        this.jobProducer = messageProducer;
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
        upload.setUserId(userId);
        upload.setVideoId(obj.getVideoId());
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
    public String uploadChunk(String uploadId,Long assetId,int partNumber,String key,String userId)
    {
        System.out.println(assetId);
        Asset upload = assetService.getAssetById(assetId);
        if(upload==null){

            throw new RuntimeException("Bad request , upload doesn't exist");
        }
       if(!upload.getUserId().equals(userId)){
           throw new RuntimeException("Bad request , upload doesn't exist");
       }
        if(!upload.getUploadStatus().equals(UploadStatus.INITIATED) ){
            throw new RuntimeException("Bad request");
        }
        return s3Service.getPreSignedURLForMultipartUploadChunk(uploadId,partNumber,key);
    }
    public boolean finishUpload(String uploadId, Long assetId,String key, Map<Integer,String> etagMap,String userId) throws JsonProcessingException {
        Asset upload = assetService.getAssetById(assetId);
        if(upload==null){
            throw new RuntimeException("Bad request , upload doesn't exist");
        }
        if(!upload.getUserId().equals(userId)){
            throw new RuntimeException("Bad request , upload doesn't exist");
        }
       TranscodingJob job = s3Service.completeMultipartUpload(uploadId,etagMap,key);
        upload.setUploadStatus(UploadStatus.COMPLETED);
        upload.setEndTime(new Timestamp(System.currentTimeMillis()));
        upload.setChunksUploaded(etagMap.size());
        job.setJobId(upload.getId().toString());

        job.setSize(upload.getFileSize());


        assetRepository.save(upload);
        // send the update to the video service ( and that video service will update the video status to uploaded and send a SSE to the frontend)
        UpdateRequestDTO dto = new UpdateRequestDTO();
        dto.setStatus("UPLOADED");
        dto.setVideoId(upload.getVideoId());
        dto.setTimestamp(String.valueOf(new Timestamp(System.currentTimeMillis())));
        kafkaProducer.produce(dto);
        // push to the transcoding_queue
        jobProducer.sendMessage(job);
        return true;
    }
    public boolean pauseUpload(Long assetId,String userId,Map<Integer,String> etagMap)
    {
        try{
           Asset upload = assetService.getAssetById(assetId);
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
            assetRepository.save(upload);
            return true;
        }
        catch(Exception e){
            return false;
        }

    }
    public boolean resumeUpload(Long assetId,String userId)
    {
        try{
            Asset upload = assetService.getAssetById(assetId);
            if(upload==null || !upload.getUserId().equals(userId))
            {
                throw new RuntimeException("Bad request , upload doesn't exist");
            }
            if(!upload.getUploadStatus().equals(UploadStatus.PAUSED)){
                throw new RuntimeException("Bad request");
            }
            upload.setUploadStatus(UploadStatus.UPLOADING);


            assetRepository.save(upload);
            return true;
        }
        catch(Exception e){
            return false;
        }
    }
}
