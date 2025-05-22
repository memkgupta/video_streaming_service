package com.vsnt.asset_onboarding.services;

import com.vsnt.asset_onboarding.entities.Asset;

import com.vsnt.asset_onboarding.entities.enums.UploadStatus;
import com.vsnt.asset_onboarding.repositories.AssetRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssetService {
    private final AssetRepository assetRepository;
    public AssetService(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    public Asset getAssetById(long id) {
        return assetRepository.findById(id);
    }
    public boolean removeAssetById(long id) {
        try{
           assetRepository.deleteById(id);
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }

        return true;
    }


}
