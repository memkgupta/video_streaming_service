package com.vsnt.user.payload.auth;

import com.vsnt.user.payload.Token.TokenResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
  private TokenResponse tokens;
  private UserDTO user;
}
