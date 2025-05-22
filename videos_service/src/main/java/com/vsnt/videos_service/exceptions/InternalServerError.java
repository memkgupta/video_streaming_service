package com.vsnt.videos_service.exceptions;

import org.springframework.http.HttpStatus;

import java.sql.Timestamp;

public class InternalServerError extends APIException{
    public InternalServerError(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR, new Timestamp(System.currentTimeMillis()));
    }
}
