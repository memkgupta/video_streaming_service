package com.vsnt.user.exceptions;

import org.springframework.http.HttpStatus;

import java.sql.Timestamp;

public class BadRequestException extends APIException {
    public BadRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST,new Timestamp(System.currentTimeMillis()));
    }
}
