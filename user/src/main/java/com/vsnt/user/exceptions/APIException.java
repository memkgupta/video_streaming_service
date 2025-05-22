package com.vsnt.user.exceptions;

import org.springframework.http.HttpStatus;

import java.sql.Timestamp;

public abstract class APIException extends RuntimeException {
    private String message;
    private Timestamp timestamp;
    private HttpStatus code;
    public APIException(String message, HttpStatus code, Timestamp timestamp) {
        super(message);
        this.message = message;
        this.timestamp = timestamp;
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public HttpStatus getCode() {
        return code;
    }

    public void setCode(HttpStatus code) {
        this.code = code;
    }
}
