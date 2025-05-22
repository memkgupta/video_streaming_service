package com.vsnt.channel_service.config;

import com.vsnt.channel_service.exceptions.APIException;
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
