package com.vsnt.asset_onboarding.exceptions;

import lombok.Data;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.sql.Timestamp;


public abstract class APIException extends RuntimeException {
    @Getter
    protected final HttpStatus status;
    @Getter
    protected String path;
    @Getter
    protected final Timestamp timestamp;
   public APIException(String message, HttpStatus status ,  String path) {
        super(message);
        this.status = status;
        this.path = path;
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }
    public APIException(String message, HttpStatus status ) {
        super(message);
        this.status = status;

        this.timestamp = new Timestamp(System.currentTimeMillis());
    }
}
