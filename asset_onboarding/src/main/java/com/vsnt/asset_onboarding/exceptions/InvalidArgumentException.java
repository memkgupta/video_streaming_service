package com.vsnt.asset_onboarding.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidArgumentException extends APIException {

    private final String field;
    private final Object rejectedValue;

    public InvalidArgumentException(String field, Object rejectedValue, String expected) {
        super(
                String.format("Invalid value '%s' for field '%s'. Expected: %s",
                        rejectedValue, field, expected),
                HttpStatus.BAD_REQUEST
        );
        this.field = field;
        this.rejectedValue = rejectedValue;
    }

    public String getField() {
        return field;
    }

    public Object getRejectedValue() {
        return rejectedValue;
    }
}