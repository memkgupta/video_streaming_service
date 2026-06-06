package com.vsnt.user.dtos.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class APIKeyResponseDTO {
    private String accessKey;
    private String secretKey;
}
