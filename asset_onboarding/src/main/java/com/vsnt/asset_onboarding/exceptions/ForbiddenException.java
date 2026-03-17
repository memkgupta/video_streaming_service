package com.vsnt.asset_onboarding.exceptions;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends APIException{
    public ForbiddenException(String action) {
        super(String.format(
                "Not authorised for performing %s",action
        ),HttpStatus.FORBIDDEN);
    }
}
