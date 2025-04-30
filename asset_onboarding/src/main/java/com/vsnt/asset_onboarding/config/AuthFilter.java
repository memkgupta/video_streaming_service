package com.vsnt.asset_onboarding.config;

import com.vsnt.asset_onboarding.dtos.AuthDTO;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Enumeration;

public class AuthFilter implements Filter {
    private final AuthenticateRequest authenticateRequest;
    public AuthFilter(AuthenticateRequest authenticateRequest) {
        this.authenticateRequest = authenticateRequest;
    }


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        System.out.println(request.getRequestURI());


try{
    Enumeration<String> headerNames = request.getHeaderNames();
    while (headerNames.hasMoreElements()) {
        String headerName = headerNames.nextElement();
        String headerValue = request.getHeader(headerName);
        System.out.println(headerName + " : " + headerValue);
    }
    System.out.println(request.getHeader("Content-Type"));
    String token = request.getHeader("Authorization").split(" ")[1];
    if(token == null) {
        throw new RuntimeException("Please login first");
    }
    AuthDTO auth = authenticateRequest.isAuthenticated(token);
    if(auth == null) {
        throw new RuntimeException("Invalid token");
    }
    System.out.println("Hello");
    request.setAttribute("userId", auth.getUserId());
    filterChain.doFilter(request, response);
}
catch (Exception e){
    e.printStackTrace();
}

    }
}
