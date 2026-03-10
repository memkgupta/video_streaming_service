package com.vsnt.channel_service.config;

import com.vsnt.channel_service.exceptions.APIException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(APIException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFound(APIException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", ex.getStatus());
        body.put("message", ex.getMessage());
        System.out.println(ex.getMessage());
        return ResponseEntity.status(HttpStatusCode.valueOf(ex.getStatus())).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAll(Exception ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", 500);
        body.put("message", "Internal server error");
        System.out.println("HEllo o am a ");
        System.out.println(Arrays.toString(ex.getStackTrace()));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
