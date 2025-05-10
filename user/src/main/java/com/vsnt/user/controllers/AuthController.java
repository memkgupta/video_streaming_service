package com.vsnt.user.controllers;

import com.vsnt.user.payload.auth.LoginRequestPayload;
import com.vsnt.user.payload.auth.LoginResponse;
import com.vsnt.user.payload.auth.UserDTO;
import com.vsnt.user.services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
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
    @GetMapping("/authenticate")
    public UserDTO authenticate(HttpServletRequest request){
        String token = request.getHeader("Authorization").split(" ")[1];
        return authService.authenticate(token);
    }
}
