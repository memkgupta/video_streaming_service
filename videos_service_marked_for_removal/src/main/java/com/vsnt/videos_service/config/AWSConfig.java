package com.vsnt.videos_service.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AWSConfig {

    private final String accessKeyId = Secrets.AWS_ACCESS_KEY_ID;
    private final String secretAccessKey = Secrets.AWS_SECRET_KEY;

    @Bean
    public S3Client s3Client() {

        AwsBasicCredentials awsCredentials =
                AwsBasicCredentials.create(accessKeyId, secretAccessKey);

        AwsCredentialsProvider credentialsProvider =
                StaticCredentialsProvider.create(awsCredentials);

        return S3Client.builder()
                .region(Region.AP_SOUTH_1)
                .credentialsProvider(credentialsProvider)
                .build();
    }
}