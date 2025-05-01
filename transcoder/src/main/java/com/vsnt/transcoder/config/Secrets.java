package com.vsnt.transcoder.config;

public class Secrets {
    public static final String AWS_SECRET_KEY = System.getenv("AWS_SECRET_KEY") ;
    public static final String AWS_ACCESS_KEY_ID = System.getenv("AWS_ACCESS_KEY_ID") ;
    public static final String AWS_RAW_BUCKET_NAME = System.getenv("AWS_RAW_BUCKET_NAME") ;
    public static final String AWS_TRANSCODED_BUCKET_NAME = System.getenv("AWS_TRANSCODED_BUCKET_NAME") ;
    public static final String DOCKER_TRANSCODER_CONTAINER_IMAGE = System.getenv("DOCKER_TRANSCODER_CONTAINER_IMAGE") ;
    public static final String CLOUD_FRONT_URL = System.getenv("CLOUD_FRONT_URL") ;
}
