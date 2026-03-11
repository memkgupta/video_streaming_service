package com.vsnt.user.dtos.organisation;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class OrganisationDTO {
    private UUID id;
    private String name;
    private String adminId;
}
