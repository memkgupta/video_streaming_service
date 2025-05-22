package com.vsnt.user.controllers;

import com.vsnt.user.payload.Token.TokenResponse;
import com.vsnt.user.services.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/token")
public class TokenController {

    private final TokenService tokenService;

    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

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
