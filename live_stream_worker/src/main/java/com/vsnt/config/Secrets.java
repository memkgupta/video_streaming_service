package com.vsnt.config;

public class Secrets {
    public static final String RABBITMQ_HOST = System.getenv("RABBITMQ_HOST");
    public static final String RABBITMQ_PORT = System.getenv("RABBITMQ_PORT");
    public static final String RABBITMQ_USER = System.getenv("RABBITMQ_USERNAME");
    public static final String RABBITMQ_PASS = System.getenv("RABBITMQ_PASSWORD");
    public static final String RABBITMQ_QUEUE = System.getenv("RABBITMQ_QUEUE");
    public static final String KAFKA_BOOTSTRAP_SERVERS =  System.getenv("KAFKA_BOOTSTRAP_SERVERS");
    public static final Integer MAX_CONCURRENT_JOBS = Integer.parseInt(System.getenv("MAX_CONCURRENT_JOBS"));
}
