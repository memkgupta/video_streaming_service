package com.vsnt.asset_onboarding.exceptions;

import org.springframework.http.HttpStatus;

public class BadRequestException extends APIException{
    public BadRequestException(String message) {
        super(message,HttpStatus.BAD_REQUEST);
    }
}
