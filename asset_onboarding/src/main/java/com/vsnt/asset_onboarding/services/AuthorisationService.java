package com.vsnt.asset_onboarding.services;

import com.vsnt.asset_onboarding.entities.Media;
import com.vsnt.asset_onboarding.entities.enums.MediaAccessibility;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthorisationService {
    private final GroupMemberService memberService;
    public AuthorisationService(GroupMemberService memberService) {
        this.memberService = memberService;
    }
    public boolean authorise(Media media ,String userId,String pullKey)
    {
        if(media.getAccessibility().equals(MediaAccessibility.PUBLIC))
        {
            return true;
        }
        else if(media.getAccessibility().equals(MediaAccessibility.PRIVATE))
        {
            //todo validate from security the pull key and media
            if(pullKey != null )
            {
                return true;
            }
        }
        else {
            if(userId!=null )
            {
                //todo check for group membership status
                return memberService.isGroupMember(
                        userId , media.getGroup()
                );
            }
        }
        return false;
    }
}
