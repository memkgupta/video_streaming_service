package com.vsnt.asset_onboarding.services;

import com.vsnt.asset_onboarding.entities.Upload;
import com.vsnt.asset_onboarding.repositories.UploadRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssetService {
    private final UploadRepository uploadRepository;
    public AssetService(UploadRepository uploadRepository) {
        this.uploadRepository = uploadRepository;
    }
    // to be cached
    public Upload getAssetById(long id) {
        return uploadRepository.findById(id);
    }
    public boolean removeAssetById(long id) {
        try{
            uploadRepository.deleteById(id);
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }

        return true;
    }
    public List<Upload> getAllAssetsByUserId(String userId) {
        return uploadRepository.findAllByUserId(userId);
    }
}
