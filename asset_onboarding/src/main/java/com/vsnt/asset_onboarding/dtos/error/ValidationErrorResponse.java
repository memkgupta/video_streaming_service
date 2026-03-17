package com.vsnt.asset_onboarding.dtos.error;
import lombok.Getter;

import java.util.List;
@Getter
public class ValidationErrorResponse extends ErrorResponse{
    private List<FieldError>  fieldErrors;
    public ValidationErrorResponse(String message, int status, String path,List<FieldError> fieldErrors) {
        super(message, status, path);
        this.fieldErrors = fieldErrors;
    }
}
