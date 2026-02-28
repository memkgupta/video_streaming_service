package com.vsnt;



import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.*;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.regions.Region;

import java.nio.file.Path;

public class S3Uploader {

    private final S3Client s3Client;

    public S3Uploader() {

        AwsBasicCredentials creds =
                AwsBasicCredentials.create(
                        AppConfig.AWS_ACCESS_KEY,
                        AppConfig.AWS_SECRET_KEY
                );

        s3Client = S3Client.builder()
                .region(Region.of("ap-south-1"))
                .credentialsProvider(
                        StaticCredentialsProvider.create(creds)
                )
                .build();
    }

    public String upload(Path file) {

        String key = "streams/" +
                AppConfig.STREAM_KEY +
                "/" +
                file.getFileName();

        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(AppConfig.S3_BUCKET)
                        .key(key)

                        .contentType("video/mp2t")
                        .build(),
                RequestBody.fromFile(file)
        );

        System.out.println("Uploaded: " + key);

        return key;
    }
}
