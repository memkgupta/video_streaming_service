package com.vsnt.asset_onboarding.services;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
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
        System.out.println(s3.getS3AccountOwner());
        InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest("springbucketdemovideo", key);
        InitiateMultipartUploadResult result = s3.initiateMultipartUpload(request);
        return result.getUploadId();
    }
    public String getPreSignedURLForMultipartUploadChunk(String uploadId,int chunkNumber,String key) {
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest("springbucketdemovideo", key)
                .withMethod(HttpMethod.PUT)
                .withContentType("application/octet-stream")

                .withExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 10));
        request.addRequestParameter("uploadId", uploadId);
        request.addRequestParameter("partNumber", String.valueOf(chunkNumber));
        URL url = s3.generatePresignedUrl(request);
        return url.toString();
    }
    public boolean completeMultipartUpload(String uploadId, Map<Integer,String> etagMap,String key)
    {
        try{
            CompleteMultipartUploadRequest request  = new CompleteMultipartUploadRequest();
            request.setUploadId(uploadId);
            request.setBucketName("springbucketdemovideo");
            request.setKey(key);
            List<PartETag> partETags = new ArrayList<>();
            for(Map.Entry<Integer,String> etag : etagMap.entrySet())
            {
                partETags.add(new PartETag(etag.getKey(), etag.getValue()));
            }
            request.setPartETags(partETags);
         s3.completeMultipartUpload(request);

        }
        catch (Exception e){
            return false;
        }
       return true;
    }
}
