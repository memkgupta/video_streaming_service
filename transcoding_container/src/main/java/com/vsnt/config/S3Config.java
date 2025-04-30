package com.vsnt.config;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

//import com.amazonaws.services;
public class S3Config {
    private String accessKey = System.getenv("ACCESS_KEY");
    private String secretKey = System.getenv("SECRET_KEY");
    private AmazonS3 client;
    public AmazonS3 getS3Client() {
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
