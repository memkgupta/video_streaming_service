package com.vsnt.asset_onboarding.config;

import com.vsnt.asset_onboarding.dtos.error.ErrorResponse;
import com.vsnt.asset_onboarding.dtos.error.FieldError;
import com.vsnt.asset_onboarding.dtos.error.ValidationErrorResponse;
import com.vsnt.asset_onboarding.exceptions.APIException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(APIException.class)
    public ResponseEntity<ErrorResponse> handleAPIException(APIException ex, HttpServletRequest request) {

        ex.printStackTrace();
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setStatus(ex.getStatus().value());
        errorResponse.setPath(request.getRequestURI());
        return ResponseEntity.status(ex.getStatus()).body(errorResponse);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex,HttpServletRequest request) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe->{
                    return new FieldError(
                            fe.getField(),fe.getRejectedValue(),fe.getDefaultMessage()
                    );
                }).toList();

        ValidationErrorResponse errorResponse = new ValidationErrorResponse(
                "Invalid Arguments",
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI(),
                fieldErrors
        );

return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception ex,HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponse.setPath(request.getRequestURI());
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

}
