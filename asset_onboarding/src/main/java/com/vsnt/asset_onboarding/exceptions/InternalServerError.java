package com.vsnt.asset_onboarding.exceptions;

public class InternalServerError extends APIException{
    public InternalServerError(String message) {
        super(message);
        status=500;
    }
}
