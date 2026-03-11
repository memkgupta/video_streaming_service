package com.vsnt.user.services;

import com.vsnt.user.entities.Organisation;
import com.vsnt.user.entities.User;
import com.vsnt.user.exceptions.UserNotFoundException;
import com.vsnt.user.repositories.OrganisationRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrganisationService {


    private final OrganisationRepository organisationRepository;
    private final UserService userService;
    public OrganisationService(OrganisationRepository organisationRepository, UserService userService) {
        this.organisationRepository = organisationRepository;
        this.userService = userService;
    }
    public Organisation createOrganisation(String name , String adminId)
    {
        User admin = userService.getUserDetails(adminId);

        Organisation organisation = new Organisation();
        organisation.setName(name);
        organisation.setAdmin(admin);
        return organisationRepository.save(organisation);
    }
    public Organisation save(Organisation organisation)
    {
        return organisationRepository.save(organisation);
    }
    public Organisation findById(UUID id )
    {
        return organisationRepository.findById(id).orElse(null);
    }
    public Organisation findByAdmin(String userId)
    {
    return organisationRepository.findByAdmin_Id(userId).orElse(null);
    }
}
