package com.mk.vsnt.moderation_service.config;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.vsnt.common_lib.Secrets;

public class S3Config {
    private static String accessKey = Secrets.AWS_ACCESS_KEY_ID;
    private static String secretKey = Secrets.AWS_SECRET_KEY;
    private static AmazonS3 client;
    private S3Config(){}
    public static AmazonS3 getS3Client() {
        System.out.println(accessKey);
        System.out.println(secretKey);

        if(client == null) {
            AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
            ClientConfiguration config = new ClientConfiguration();
            config.setProtocol(Protocol.HTTP);
            client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(credentials))
                    .withClientConfiguration(config)
                    .withRegion(Regions.AP_SOUTH_1).build();
        }

        return client;
    }

}