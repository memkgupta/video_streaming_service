package com.vsnt.aggregatorservice.config;

public class APIException extends RuntimeException {
    int status;
    public APIException(String message,int status) {
        super(message);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}