package com.vsnt.user.payload.Token;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TokenResponse {
    private String token;

}
