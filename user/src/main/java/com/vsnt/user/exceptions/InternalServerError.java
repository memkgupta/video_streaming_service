package com.vsnt.user.exceptions;

import org.springframework.http.HttpStatus;

import java.sql.Timestamp;

public class InternalServerError extends APIException{
    public InternalServerError(String message) {
        super(message);
        status=500;
    }
}
