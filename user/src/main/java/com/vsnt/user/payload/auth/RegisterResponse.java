package com.vsnt.user.payload.auth;

import com.vsnt.user.payload.Token.TokenResponse;
import lombok.Data;

import java.sql.Timestamp;
@Data
public class RegisterResponse {

private UserDTO user;
    private TokenResponse tokens;
}
