package com.vsnt.asset_onboarding;

import com.vsnt.asset_onboarding.entities.Asset;
import com.vsnt.asset_onboarding.entities.AssetAESKey;
import com.vsnt.asset_onboarding.entities.Media;
import com.vsnt.asset_onboarding.services.KeyService;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class  AssetCreationStrategy<M> {
    private final KeyService keyService;

    protected AssetCreationStrategy(KeyService keyService) {
        this.keyService = keyService;
    }

    abstract Asset helper(Media media,M metadata);
    public Asset createAsset(Media media, M metadata){
        Asset nAsset = helper(media,metadata);
        try{

            keyService.generateKey(nAsset.getId().toString());

        }catch(Exception e){
            e.printStackTrace();
        }
        return nAsset;
    }
}
