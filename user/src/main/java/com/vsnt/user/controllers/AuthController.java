package com.vsnt.user.controllers;

import com.vsnt.user.config.KafkaProducer;
import com.vsnt.user.payload.ChannelPayload;
import com.vsnt.user.payload.auth.*;
import com.vsnt.user.services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Endpoints for user login, registration, and token validation")
public class AuthController {
    private final AuthService authService;
    private final KafkaProducer kafkaProducer;

    public AuthController(AuthService authService, KafkaProducer kafkaProducer) {
        this.authService = authService;
        this.kafkaProducer = kafkaProducer;
    }

    @Operation(summary = "Login user", description = "Authenticates a user with email and password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequestPayload userDTO) {
        return ResponseEntity.ok(authService.login(userDTO.getEmail(), userDTO.getPassword()));
    }

    @Operation(summary = "Register user", description = "Registers a new user and creates their channel")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registration successful"),
            @ApiResponse(responseCode = "400", description = "Invalid registration data")
    })
    @PostMapping("/register")
    public RegisterResponse register(@RequestBody RegisterRequest userDTO) {
        RegisterResponse response = authService.register(userDTO);

        ChannelPayload payload = new ChannelPayload();
        UserDTO created = response.getUser();
        payload.setName(created.getName());
        payload.setHandle(created.getId());
        payload.setUserId(created.getId());
        payload.setProfile(created.getAvatar());

        kafkaProducer.produce(payload);
        return response;
    }

    @Operation(summary = "Authenticate token", description = "Returns user info based on the JWT provided")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token valid"),
            @ApiResponse(responseCode = "401", description = "Invalid or missing token")
    })
    @GetMapping("/authenticate")
    public UserDTO authenticate(HttpServletRequest request) {
        String token = request.getHeader("Authorization").split(" ")[1];
        return authService.authenticate(token);
    }
}
