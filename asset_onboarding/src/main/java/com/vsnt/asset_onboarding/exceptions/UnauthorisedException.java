package com.vsnt.asset_onboarding.exceptions;

import org.springframework.http.HttpStatus;

public class UnauthorisedException extends APIException{
    public UnauthorisedException(String action) {
        super(
                String.format("Not authorised to perform %s", action),HttpStatus.UNAUTHORIZED
        );
    }
}
