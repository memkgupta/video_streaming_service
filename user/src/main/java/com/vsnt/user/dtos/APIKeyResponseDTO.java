package com.vsnt.user.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class APIKeyResponseDTO {
    private String accessKey;
    private String secretKey;
}
