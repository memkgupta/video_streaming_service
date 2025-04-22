package com.vsnt.asset_onboarding.config;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AWSConfig {
 @Value("${aws.accesskey}")
    String accessKeyId;
 @Value("${aws.secret}")
    String secretAccessKey;
 @Bean
    public AmazonS3 getS3Client() {
        AWSCredentials credentials = new BasicAWSCredentials(accessKeyId, secretAccessKey);
     ClientConfiguration config = new ClientConfiguration();
     config.setProtocol(Protocol.HTTP);
        AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withClientConfiguration(config)
                .withRegion(Regions.AP_SOUTH_1).build();
        return s3;
    }
}
