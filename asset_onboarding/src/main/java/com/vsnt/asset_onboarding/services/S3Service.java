package com.vsnt.asset_onboarding.services;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.vsnt.asset_onboarding.config.Secrets;
import com.vsnt.asset_onboarding.dtos.TranscodingJob;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;

import java.io.ByteArrayInputStream;
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

    public String generatePresignedUrl(String bucketName, String key)
    {
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, key);

        URL url = s3.generatePresignedUrl(request);
        return url.toString();
    }
    public String startSingleUpload(String key , String fileType)
    {

       GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(Secrets.AWS_BUCKET_NAME,key)
               .withMethod(HttpMethod.PUT)
               .withContentType(fileType);
       URL url = s3.generatePresignedUrl(request);
       return url.toString();

    }
    public String startMultiPartUpload(String key) {

        InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(Secrets.AWS_BUCKET_NAME, key);
        InitiateMultipartUploadResult result = s3.initiateMultipartUpload(request);
        return result.getUploadId();
    }
    public String startMultiPartUpload(String key, String bucketName) {

        InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(bucketName, key);
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
    public String getPreSignedURLForMultipartUploadChunk(String uploadId,int chunkNumber,String key , String bucketName) {
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, key)
                .withMethod(HttpMethod.PUT)
                .withContentType("application/octet-stream");


        request.addRequestParameter("uploadId", uploadId);
        request.addRequestParameter("partNumber", String.valueOf(chunkNumber));
        URL url = s3.generatePresignedUrl(request);
        return url.toString();
    }
    public void completeMultipartUpload(String uploadId, Map<Integer,String> etagMap, String key)
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
        s3.completeMultipartUpload(request);


        }
        catch (Exception e){
            e.printStackTrace();

        }


    }
    public String uploadFileToS3(String bucketName,String key , byte[] body)
    {

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(body.length);
        metadata.setContentType("application/octet-stream");

        // Upload to S3
        ByteArrayInputStream inputStream =
                new ByteArrayInputStream(body);

        s3.putObject(
                bucketName,
                key,
                inputStream,
                metadata
        );

       return Secrets.CDN_RESOURCE_URL+key;
    }
}
