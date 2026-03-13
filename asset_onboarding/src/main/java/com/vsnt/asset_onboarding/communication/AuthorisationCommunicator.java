package com.vsnt.asset_onboarding.communication;

import com.vsnt.asset_onboarding.feign.AuthorisationClient;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthorisationCommunicator {
    private final AuthorisationClient authorisationClient;

    public AuthorisationCommunicator(AuthorisationClient authorisationClient) {
        this.authorisationClient = authorisationClient;
    }

    public boolean isGroupMember(String userId , UUID groupId)
    {
    return authorisationClient.isMember(userId,groupId);
    }
}
