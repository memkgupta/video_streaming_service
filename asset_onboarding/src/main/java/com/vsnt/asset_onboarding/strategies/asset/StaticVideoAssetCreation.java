package com.vsnt.asset_onboarding.strategies.asset;

import com.vsnt.asset_onboarding.AssetCreationStrategy;
import com.vsnt.asset_onboarding.config.Secrets;
import com.vsnt.asset_onboarding.dtos.FileMetaData;
import com.vsnt.asset_onboarding.entities.Asset;
import com.vsnt.asset_onboarding.entities.Media;
import com.vsnt.asset_onboarding.entities.enums.AssetType;
import com.vsnt.asset_onboarding.entities.enums.UploadStatus;
import com.vsnt.asset_onboarding.repositories.AssetRepository;
import com.vsnt.asset_onboarding.services.KeyService;
import com.vsnt.asset_onboarding.services.S3Service;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.UUID;

@Component
public class StaticVideoAssetCreation extends AssetCreationStrategy<FileMetaData> {
    private final AssetRepository assetRepository;
    private final S3Service s3Service;
    protected StaticVideoAssetCreation(KeyService keyService, AssetRepository assetRepository, S3Service s3Service) {
        super(keyService,true);
        this.assetRepository = assetRepository;
        this.s3Service = s3Service;
    }

    @Override
    public Asset helper(Media media, FileMetaData metadata) {
        String fileName = metadata.getFileName();
        String fileUploadId = UUID.randomUUID().toString();
        String key = "uploads/"+media.getId()+"/video"+"/"+fileUploadId+".mp4";
        String uploadId = s3Service.startMultiPartUpload(key, Secrets.AWS_SECURE_BUCKET);
        Asset asset = new Asset();
        asset.setFileUploadId(fileUploadId);
        asset.setFileName(fileName);
        asset.setChunksUploaded(0);
        asset.setUploadStatus(UploadStatus.INITIATED);
        asset.setUploadId(uploadId);
        asset.setAssetType(AssetType.VIDEO);
        asset.setStartTime(new Timestamp(System.currentTimeMillis()));
        asset.setMediaId(media.getId());
        asset.setKey(key);
        asset.setFileSize(metadata.getFileSize());
        asset.setFileType(metadata.getFileType());
        return assetRepository.save(asset);
    }
}
