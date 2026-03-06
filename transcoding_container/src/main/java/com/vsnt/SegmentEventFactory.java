package com.vsnt;

import com.vsnt.dtos.MediaType;
import com.vsnt.dtos.ResolutionEnum;
import com.vsnt.dtos.TranscodingSegmentUpdateDTO;
import com.vsnt.services.S3Service;

import java.nio.file.Path;

public class SegmentEventFactory {

    private final String assetId;
    private final String mediaId;
    private final MediaType mediaType;
    private final String cdnBaseUrl;
    private final long segmentDuration;
    private final S3Service s3Service;
    private final String transcodedBucketName;
    public SegmentEventFactory(String assetId,
                               String mediaId, MediaType mediaType,
                               String cdnBaseUrl,
                               long segmentDuration, S3Service s3Service, String transcodedBucketName) {
        this.assetId = assetId;
        this.mediaId = mediaId;
        this.mediaType = mediaType;
        this.cdnBaseUrl = cdnBaseUrl;
        this.segmentDuration = segmentDuration;
        this.s3Service = s3Service;
        this.transcodedBucketName = transcodedBucketName;
    }

    public TranscodingSegmentUpdateDTO generate(Path segmentPath) {
        String fileName = segmentPath.getFileName().toString();
        // Example: segment005.ts → 5
        long sequenceNumber = extractSequenceNumber(fileName);
        // resolution from folder name (360p, 480p etc.)
        String resolutionFolder = segmentPath.getParent().getFileName().toString();
        ResolutionEnum resolution =
                ResolutionEnum.valueOf("RESOLUTION_"+resolutionFolder.replace("p", "P"));
       String s3Key = "transcoded/"+assetId+"/"+resolution.toString()+"/"+fileName;
        try{
            s3Service.uploadSegment(
                    transcodedBucketName ,
                    s3Key,
                    segmentPath
            );
            String url = cdnBaseUrl + "/" +s3Key;
            return new TranscodingSegmentUpdateDTO(
                    assetId,
                    url,
                    sequenceNumber,
                    mediaId,
                    segmentDuration,
                    resolution,
                    mediaType
            );
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    private long extractSequenceNumber(String fileName) {
        String number = fileName
                .replace("segment", "")
                .replace(".ts", "");
        return Long.parseLong(number);
    }
}
