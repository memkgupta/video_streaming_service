package com.vsnt;

public class AppConfig {

    public static final String STREAM_KEY =
            System.getenv("STREAM_KEY");

    public static final String OUTPUT_DIR =
            System.getenv().getOrDefault("OUTPUT_DIR", "/output");

    public static final String S3_BUCKET =
            System.getenv("S3_BUCKET");

    public static final String AWS_ACCESS_KEY =
            System.getenv("AWS_ACCESS_KEY");

    public static final String AWS_SECRET_KEY =
            System.getenv("AWS_SECRET_KEY");

    public static final String AWS_REGION =
            System.getenv().getOrDefault("AWS_REGION", "ap-south-1");

    public static final String KAFKA_BOOTSTRAP =
            System.getenv("KAFKA_BOOTSTRAP");

    public static final String KAFKA_TOPIC =
            System.getenv().getOrDefault("KAFKA_TOPIC", "segment-created");

    public static final String CDN_BASE_URL =
            System.getenv("CDN_BASE_URL");
}