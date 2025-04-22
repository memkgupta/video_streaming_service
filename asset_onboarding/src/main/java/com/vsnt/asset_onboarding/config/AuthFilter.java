package com.vsnt.asset_onboarding.config;

import com.vsnt.asset_onboarding.dtos.AuthDTO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class AuthFilter extends OncePerRequestFilter {
    private final AuthenticateRequest authenticateRequest;
    public AuthFilter(AuthenticateRequest authenticateRequest) {
        this.authenticateRequest = authenticateRequest;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getParameter("Authorisation").split(" ")[1];
        if(token == null) {
            throw new RuntimeException("Please login first");
        }
        AuthDTO auth = authenticateRequest.isAuthenticated(token);
        if(auth == null) {
            throw new RuntimeException("Invalid token");
        }
        request.setAttribute("userId", auth.getUserId());
        filterChain.doFilter(request, response);
    }
}
