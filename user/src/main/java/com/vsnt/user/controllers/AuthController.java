package com.vsnt.user.controllers;

import com.vsnt.user.config.KafkaProducer;
import com.vsnt.user.payload.ChannelPayload;
import com.vsnt.user.payload.auth.*;
import com.vsnt.user.services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final KafkaProducer kafkaProducer;

    public AuthController(AuthService authService, KafkaProducer kafkaProducer) {
        this.authService = authService;
        this.kafkaProducer = kafkaProducer;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequestPayload userDTO){

        return ResponseEntity.ok(authService.login(userDTO.getEmail(), userDTO.getPassword()));
    }
    @PostMapping("/register")
    public RegisterResponse register(@RequestBody RegisterRequest userDTO){
      RegisterResponse response = authService.register(userDTO);
        ChannelPayload payload = new ChannelPayload();
       UserDTO created= response.getUser();
       payload.setName(created.getName());
       payload.setHandle(created.getId());
       payload.setUserId(created.getId());
       payload.setProfile(created.getAvatar());
      kafkaProducer.produce(payload);
      return response;
    }
    @GetMapping("/authenticate")
    public UserDTO authenticate(HttpServletRequest request){
        String token = request.getHeader("Authorization").split(" ")[1];
        return authService.authenticate(token);
    }
}
