package com.vsnt.user.services;

import com.vsnt.user.entities.Group;
import com.vsnt.user.entities.GroupMember;
import com.vsnt.user.entities.Organisation;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthorisationService {
    private final GroupService groupService;
    private final OrganisationService organisationService;
    public AuthorisationService(GroupService groupService, OrganisationService organisationService) {
        this.groupService = groupService;

        this.organisationService = organisationService;
    }

    public boolean isOrgAdmin(String userId , UUID orgId)
    {
        Organisation organisation = organisationService.findById(orgId);
        if(organisation == null)
        {
            throw new RuntimeException("Organisation Not Found");
        }
        return organisation.getAdmin().getId().equals(userId);

    }
    public boolean isGroupMember(String userId , UUID groupId)
    {
        Group group = groupService.getGroup(groupId);
        if(group == null)
        {
            throw new RuntimeException("Group Not Found");
        }
        return groupService.findGroups(userId).contains(group);
    }

}
