package com.vsnt.user.payload.auth;

import lombok.Data;

@Data
public class LoginRequestPayload {
    private String email;
    private String password;
}
