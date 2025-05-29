package com.vsnt.asset_onboarding.services;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.vsnt.asset_onboarding.config.Secrets;
import com.vsnt.asset_onboarding.dtos.TranscodingJob;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service

public class S3Service {
    private final AmazonS3 s3;

    public S3Service(AmazonS3 s3) {
        this.s3 = s3;
    }

    public String startMultiPartUpload(String key) {

        InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(Secrets.AWS_BUCKET_NAME, key);
        InitiateMultipartUploadResult result = s3.initiateMultipartUpload(request);
        return result.getUploadId();
    }
    public String getPreSignedURLForMultipartUploadChunk(String uploadId,int chunkNumber,String key) {
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(Secrets.AWS_BUCKET_NAME, key)
                .withMethod(HttpMethod.PUT)
                .withContentType("application/octet-stream");


        request.addRequestParameter("uploadId", uploadId);
        request.addRequestParameter("partNumber", String.valueOf(chunkNumber));
        URL url = s3.generatePresignedUrl(request);
        return url.toString();
    }
    public TranscodingJob completeMultipartUpload(String uploadId, Map<Integer,String> etagMap, String key)
    {
        try{
            CompleteMultipartUploadRequest request  = new CompleteMultipartUploadRequest();
            request.setUploadId(uploadId);
            request.setBucketName(Secrets.AWS_BUCKET_NAME);
            request.setKey(key);
            List<PartETag> partETags = new ArrayList<>();
            for(Map.Entry<Integer,String> etag : etagMap.entrySet())
            {
                partETags.add(new PartETag(etag.getKey(), etag.getValue()));
            }
            request.setPartETags(partETags);
        var e = s3.completeMultipartUpload(request);

            TranscodingJob job = new TranscodingJob();
            job.setKey(e.getKey());
            return job;
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }


    }
}
