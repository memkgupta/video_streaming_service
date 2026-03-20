package com.vsnt.asset_onboarding.config;

public class Secrets {
    public static final String AWS_ACCESS_KEY_ID = System.getenv("AWS_ACCESS_KEY_ID");
    public static final String AWS_SECRET_KEY = System.getenv("AWS_SECRET_KEY");
    public static final String AWS_BUCKET_NAME = System.getenv("AWS_BUCKET_NAME");
    public static final String CLOUDFRONT_KEY_PAIR_ID =
            System.getenv("CLOUDFRONT_KEY_PAIR_ID");
    public static final String PRIVATE_KEY_PATH =
            System.getenv("PRIVATE_KEY_PATH");
    public static final String CDN_RESOURCE_URL =
            System.getenv("CDN_RESOURCE_URL");

    public static final String AWS_SECURE_BUCKET =
    System.getenv("AWS_SECURE_BUCKET");
    public static final String KAFKA_BOOTSTRAP_SERVERS =
            System.getenv("KAFKA_URL");
}
