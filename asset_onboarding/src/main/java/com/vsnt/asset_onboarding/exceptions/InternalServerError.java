package com.vsnt.asset_onboarding.exceptions;

import org.springframework.http.HttpStatus;

public class InternalServerError extends APIException{
    public InternalServerError() {
       super("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
