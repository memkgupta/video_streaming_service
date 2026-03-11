package com.vsnt.user.dtos;

import lombok.Data;

import java.util.UUID;

@Data
public class APIkeyAuthRequest {
private UUID accessKey;
private String secretKey;
}
