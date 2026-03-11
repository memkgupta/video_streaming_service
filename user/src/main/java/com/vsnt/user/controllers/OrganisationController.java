package com.vsnt.user.controllers;

import com.vsnt.user.dtos.APIKeyResponseDTO;
import com.vsnt.user.dtos.organisation.OrganisationDTO;
import com.vsnt.user.entities.APIKey;
import com.vsnt.user.entities.Organisation;
import com.vsnt.user.services.APIKeyService;
import com.vsnt.user.services.OrganisationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/organisation")
public class OrganisationController {
    private final OrganisationService organisationService;
    private final APIKeyService apiKeyService;
    public OrganisationController(OrganisationService organisationService, APIKeyService apiKeyService) {
        this.organisationService = organisationService;
        this.apiKeyService = apiKeyService;
    }
    @PostMapping("/api-key")
    public ResponseEntity<APIKeyResponseDTO> generateAPIKey(@RequestHeader("X-USER-ID") String userId) {

        Organisation organisation = organisationService.findByAdmin(userId);
        if(organisation == null)
        {
            throw new RuntimeException("Organisation Not Found");
        }
        APIKey apiKey =apiKeyService.generateAPIKey(organisation);
        return ResponseEntity.ok(APIKeyResponseDTO.builder().accessKey(apiKey.getAccessKey().toString())
                .secretKey(apiKey.getSecret()).build());
    }
    @PostMapping
    public ResponseEntity<OrganisationDTO> createOrganisation(@RequestBody OrganisationDTO organisationDTO,@RequestHeader("X-USER-ID") String userId) {
        if(organisationService.findByAdmin(userId)!=null)
        {
            throw new  RuntimeException("Organisation Already Exists");
        }
        Organisation organisation = organisationService.createOrganisation(
                organisationDTO.getName(),organisationDTO.getAdminId()
        );
        return ResponseEntity.ok(OrganisationDTO.builder()
                .adminId(organisationDTO.getAdminId())
                .id(organisation.getId())
                .name(organisation.getName())
                .build());
    }

}
