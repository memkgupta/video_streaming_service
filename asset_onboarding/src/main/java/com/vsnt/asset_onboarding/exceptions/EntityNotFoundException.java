package com.vsnt.asset_onboarding.exceptions;

public class EntityNotFoundException extends APIException{
    public EntityNotFoundException(String entityName) {
        super(entityName+" "+"not found");
        super.status  =404;
    }
}
