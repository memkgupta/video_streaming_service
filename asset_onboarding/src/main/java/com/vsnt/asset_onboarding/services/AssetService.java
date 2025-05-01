package com.vsnt.asset_onboarding.services;

import com.vsnt.asset_onboarding.entities.Upload;
import com.vsnt.asset_onboarding.entities.enums.UploadStatus;
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
    public void updateAssetStatus(long id, String status)
    {
        Upload upload = uploadRepository.findById(id);
        if(upload==null){
            System.out.println("Invalid update");
            return;
        }
        upload.setUploadStatus(status.equals("SUCCESS")?UploadStatus.COMPLETED:UploadStatus.FAILED);


    }
}
