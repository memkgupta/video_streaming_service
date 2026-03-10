package com.vsnt.videos_service.config;
public class Secrets {
    public static final String AWS_ACCESS_KEY_ID = System.getenv("AWS_ACCESS_KEY_ID");
    public static final String AWS_SECRET_KEY = System.getenv("AWS_SECRET_KEY");
    public static final String AWS_REGION = System.getenv("AWS_REGION");
    public static final String PLAYLIST_BUCKET_NAME = System.getenv("PLAYLIST_BUCKET_NAME");
    public static final String CDN_URL_NAME = System.getenv("CDN_URL_NAME");
}
