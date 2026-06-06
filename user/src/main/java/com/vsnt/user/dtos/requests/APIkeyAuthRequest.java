package com.vsnt.user.dtos.requests;

import lombok.Data;

import java.util.UUID;

@Data
public class APIkeyAuthRequest {
private UUID accessKey;
private String secretKey;
}
