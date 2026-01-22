package com.vsnt.asset_onboarding.services;

import com.vsnt.asset_onboarding.dtos.AssetChunk;
import com.vsnt.asset_onboarding.entities.Asset;

import com.vsnt.asset_onboarding.entities.enums.UploadStatus;
import com.vsnt.asset_onboarding.repositories.AssetRepository;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AssetService {
    private final AssetRepository assetRepository;
    private final static int CHUNK_SIZE_MB =1;
    public AssetService(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    public Asset getAssetById(long id) {
        return assetRepository.findById(id);
    }
    public Asset updateAssetUrl(String id ,String url) {
        Asset asset = assetRepository.findByVideoId(id);
        asset.setUrl(url);
        return assetRepository.save(asset);
    }
    public Asset getAssetByVideoId(String videoId) {
        return assetRepository.findByVideoId(videoId);
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
    public List<AssetChunk> splitIntoChunks(String assetId)
    {
        List<AssetChunk> assetChunks = new ArrayList<>();
        long chunkSize = CHUNK_SIZE_MB* 1024L * 1024L;
        Asset asset = assetRepository.findByVideoId(assetId);
        if(asset==null)
        {
            throw new RuntimeException("Asset Not Found");
        }
        int chunk_number = 0;
        for(long start = 0;start< asset.getFileSize();start+=chunkSize)
        {
            long end = Math.min(start+chunkSize,asset.getFileSize());
            long size = end - start +1;
            assetChunks.add(
                    AssetChunk.builder()
                            .chunkId(chunk_number++)
                            .size(size)
                            .assetId(assetId)
                            .end(end)
                            .start(start)
                            .build()
            );
        }
        asset.setChunks(assetChunks);
        assetRepository.save(asset);
        return assetChunks;
    }

}
