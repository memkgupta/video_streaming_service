package com.vsnt.asset_onboarding.config;

public class Secrets {
    public static final String AWS_ACCESS_KEY_ID = System.getenv("AWS_ACCESS_KEY_ID");
    public static final String AWS_SECRET_KEY = System.getenv("AWS_SECRET_KEY");
    public static final String AWS_REGION = System.getenv("AWS_REGION");
    public static final String AWS_BUCKET_NAME = System.getenv("AWS_BUCKET_NAME");

}
