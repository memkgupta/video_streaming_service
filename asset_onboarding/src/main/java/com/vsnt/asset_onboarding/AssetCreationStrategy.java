package com.vsnt.asset_onboarding;

import com.vsnt.asset_onboarding.entities.Asset;
import com.vsnt.asset_onboarding.entities.AssetAESKey;
import com.vsnt.asset_onboarding.entities.Media;
import com.vsnt.asset_onboarding.repositories.MediaRepository;
import com.vsnt.asset_onboarding.services.KeyService;
import com.vsnt.asset_onboarding.services.MediaService;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class  AssetCreationStrategy<M> {
    private final KeyService keyService;
    private final boolean isSecuredAsset;

    protected AssetCreationStrategy(KeyService keyService, boolean isSecuredAsset) {
        this.keyService = keyService;
        this.isSecuredAsset = isSecuredAsset;

    }


    protected abstract Asset helper(Media media,M metadata);
    public Asset createAsset(Media media, M metadata){

        Asset nAsset = helper(media,metadata);
        if(isSecuredAsset){
            try{
                keyService.generateKey(nAsset.getId().toString());
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        media.setVideoAsset(nAsset);

        return nAsset;
    }
}
