package com.vsnt.asset_onboarding.services;

import com.vsnt.asset_onboarding.communication.AuthorisationCommunicator;
import com.vsnt.asset_onboarding.entities.Media;
import com.vsnt.asset_onboarding.entities.MediaPullKey;
import com.vsnt.asset_onboarding.entities.MediaPushKey;
import com.vsnt.asset_onboarding.entities.enums.MediaAccessibility;
import com.vsnt.asset_onboarding.repositories.MediaPullKeyRepository;
import com.vsnt.asset_onboarding.repositories.MediaPushKeyRepository;
import com.vsnt.asset_onboarding.strategies.delivery.DeliverySecurityConfig;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class AuthorisationService {

    private final AuthorisationCommunicator communicator;
    private final DeliverySecurityConfig deliverySecurityConfig;
    private final MediaService mediaService;
    public AuthorisationService(AuthorisationCommunicator communicator, DeliverySecurityConfig deliverySecurityConfig, MediaService mediaService) {

        this.communicator = communicator;
        this.deliverySecurityConfig = deliverySecurityConfig;
        this.mediaService = mediaService;
    }
    public boolean canPush(Media media , String pushKey)
    {
        MediaPushKey fetched = media.getPushKey();
        if(!fetched.isActive())
        {
return false;        }
        return fetched.getKey().equals(pushKey);
    }
    public boolean canPush(String assetId , String pushKey)
    {
        Media media = mediaService.getMediaByAsset(Long.parseLong(assetId));
        if(media==null)
        {
            return false;
        }
        return canPush(media , pushKey);
    }

    public boolean canWatch(Media media , String access_token)
    {
    return deliverySecurityConfig.validateToken(access_token,media.getVideoAsset().getId().toString());
    }
    public boolean canWatch(String assetId , String access_token)
    {
        return deliverySecurityConfig.validateToken(access_token,assetId);
    }

}
