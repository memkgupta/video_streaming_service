package com.vsnt.asset_onboarding.services;

import com.vsnt.asset_onboarding.entities.AssetAESKey;
import com.vsnt.asset_onboarding.exceptions.EntityNotFoundException;
import com.vsnt.asset_onboarding.repositories.AssetAESKeyRepository;
import com.vsnt.asset_onboarding.utils.AESKeyGenerator;
import org.springframework.stereotype.Service;

@Service
public class KeyService {
private final AssetAESKeyRepository assetAESKeyRepository;
private final AESKeyGenerator aesKeyGenerator;

    public KeyService(AssetAESKeyRepository assetAESKeyRepository, AESKeyGenerator aesKeyGenerator) {
        this.assetAESKeyRepository = assetAESKeyRepository;
        this.aesKeyGenerator = aesKeyGenerator;
    }

    public void generateKey(String assetId) throws Exception {
    String keyURL = aesKeyGenerator.generateKey(assetId);
    AssetAESKey assetAESKey = new AssetAESKey();
    assetAESKey.setKeyURL(keyURL);
    assetAESKey.setAssetID(assetId);
        assetAESKeyRepository.save(assetAESKey);
    }
public AssetAESKey getKey(String assetID)  {
        return assetAESKeyRepository.findByAssetID(assetID).orElseThrow(() -> new EntityNotFoundException("Key",assetID));
}
}
