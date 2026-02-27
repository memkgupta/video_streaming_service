package com.vsnt.videos_service.services;


import com.vsnt.videos_service.config.Secrets;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class S3Service {


    private final S3Client s3Client;

    private final String bucketName = Secrets.PLAYLIST_BUCKET_NAME;

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public void uploadPlaylist(String key, String content) {

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType("application/vnd.apple.mpegurl")
                .cacheControl("max-age=3600")
                .build();

        s3Client.putObject(
                request,
                RequestBody.fromString(content)
        );
    }
}
