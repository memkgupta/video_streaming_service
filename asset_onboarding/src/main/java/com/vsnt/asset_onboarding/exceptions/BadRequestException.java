package com.vsnt.asset_onboarding.exceptions;

public class BadRequestException extends APIException{
    public BadRequestException(String message) {
        super(message);
        super.status=401;
    }
}
