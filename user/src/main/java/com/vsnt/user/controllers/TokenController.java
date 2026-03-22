package com.vsnt.user.controllers;

import com.vsnt.user.payload.Token.TokenResponse;
import com.vsnt.user.services.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/v1/token")
@Tag(name = "Token Management", description = "Handles access token refresh operations")
public class TokenController {

    private final TokenService tokenService;

    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Operation(
            summary = "Refresh Access Token",
            description = "Generates a new access token using a valid refresh token sent in the 'X-REFRESH-TOKEN' header"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Access token refreshed successfully"),
            @ApiResponse(responseCode = "400", description = "Refresh token header is missing or invalid"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/refresh-token")
    public ResponseEntity<TokenResponse> refreshAccessToken(HttpServletRequest request) {
        if(request.getHeader("X-REFRESH-TOKEN") == null) {
            throw new RuntimeException("X-REFRESH-TOKEN header is null");
        }
        String token = request.getHeader("X-REFRESH-TOKEN");
        return ResponseEntity.ok(TokenResponse.builder()
                .accessToken(tokenService.refreshToken(token))
                .build());
    }
}
