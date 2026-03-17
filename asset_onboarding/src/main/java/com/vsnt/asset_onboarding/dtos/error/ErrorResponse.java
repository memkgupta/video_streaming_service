package com.vsnt.asset_onboarding.dtos.error;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
@Getter
@Setter
public class ErrorResponse {

    private String message;
    private int status;
    private String path;
    private String errorCode;
    private long timestamp;


    public ErrorResponse() {
        this.timestamp = Instant.now().toEpochMilli();
    }

    public ErrorResponse(String message, int status, String path) {
        this.message = message;
        this.status = status;
        this.path = path;
        this.timestamp = Instant.now().toEpochMilli();
    }

    public ErrorResponse(String message, int status, String path, String errorCode) {
        this(message, status, path);
        this.errorCode = errorCode;
    }





}