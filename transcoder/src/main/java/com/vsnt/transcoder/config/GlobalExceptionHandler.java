package com.vsnt.transcoder.config;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.converter.HttpMessageNotReadableException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        String errorMsg = "Invalid request format: " + extractRootCause(ex);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorMsg);
    }

    private String extractRootCause(Throwable ex) {
        Throwable root = ex;
        while (root.getCause() != null && root != root.getCause()) {
            root = root.getCause();
        }
        return root.getMessage();
    }
}