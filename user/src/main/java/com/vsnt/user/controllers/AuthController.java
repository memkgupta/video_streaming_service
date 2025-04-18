package com.vsnt.user.controllers;

import com.vsnt.user.payload.auth.LoginRequestPayload;
import com.vsnt.user.payload.auth.LoginResponse;
import com.vsnt.user.payload.auth.UserDTO;
import com.vsnt.user.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequestPayload userDTO){

        return ResponseEntity.ok(authService.login(userDTO.getEmail(), userDTO.getPassword()));
    }
    @PostMapping("/register")
    public String register(@RequestBody UserDTO userDTO){
        return authService.register(userDTO);
    }
}
