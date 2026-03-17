package com.vsnt.asset_onboarding.exceptions;

import org.springframework.http.HttpStatus;

public class EntityNotFoundException extends APIException{
    public EntityNotFoundException(String entityName,String identifier ) {
        super(String.format("%s not found with identifier %s",entityName,identifier), HttpStatus.NOT_FOUND);

    }
}
