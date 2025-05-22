package com.vsnt.user.config;

import com.vsnt.user.exceptions.APIException;
import com.vsnt.user.exceptions.InternalServerError;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = APIException.class)
    public ResponseEntity<APIException> handleAPIException(APIException e) {
        return  ResponseEntity.status(e.getCode()).body(e);
    }

}
