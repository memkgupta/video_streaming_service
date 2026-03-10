package com.vsnt.asset_onboarding.listeners;

import com.vsnt.asset_onboarding.SecuredCDNService;
import com.vsnt.asset_onboarding.config.ModerationJobProducer;
import com.vsnt.asset_onboarding.config.TranscodingJobMessageProducer;
import com.vsnt.asset_onboarding.dtos.TranscodingJob;
import com.vsnt.asset_onboarding.entities.Asset;
import com.vsnt.asset_onboarding.entities.AssetAESKey;
import com.vsnt.asset_onboarding.entities.Media;
import com.vsnt.asset_onboarding.entities.enums.AssetType;
import com.vsnt.asset_onboarding.services.KeyService;
import com.vsnt.asset_onboarding.services.MediaService;
import com.vsnt.common_lib.dtos.ModerationJob;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class VideoUploadHandler implements AssetUploadHandler{
    private final KeyService keyService;
    private final MediaService mediaService;
    private final SecuredCDNService securedCDNService;
    private final TranscodingJobMessageProducer transcodingJobMessageProducer;
    private final ModerationJobProducer  moderationJobProducer;
    public VideoUploadHandler(KeyService keyService, MediaService mediaService, SecuredCDNService securedCDNService, TranscodingJobMessageProducer transcodingJobMessageProducer, ModerationJobProducer moderationJobProducer) {
        this.keyService = keyService;
        this.mediaService = mediaService;
        this.securedCDNService = securedCDNService;
        this.transcodingJobMessageProducer = transcodingJobMessageProducer;
        this.moderationJobProducer = moderationJobProducer;
    }

    @Override
    public void handle(Asset asset) {
        Media media = mediaService.getMedia(asset.getMediaId());

        if(media.isModerationEnabled())
        {
            ModerationJob moderationJob = new ModerationJob();
            moderationJob.setJobId(media.getId().toString());
            moderationJob.setFileKey(asset.getKey());
            moderationJob.setSize(asset.getFileSize());
            moderationJobProducer.sendMessage(moderationJob);
        }
        else {
            AssetAESKey assetKey = null;
            try {
                assetKey = keyService.getKey(asset.getId().toString());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            byte[] encryptionKey = securedCDNService.fetchSecure(
                    assetKey.getKeyURL()
            );
            String encodedKey  = Base64.getEncoder().encodeToString(encryptionKey);
            TranscodingJob job =
                    TranscodingJob.builder()
                            .key(asset.getKey())
                            .encryptionKey(encodedKey)
                            .jobId(media.getId().toString())
                            .assetId(asset.getId().toString())
                            .build();
            transcodingJobMessageProducer.sendMessage(job);
        }

        //todo add user auth check to upload the chunk
    }

    @Override
    public boolean supports(AssetType assetType) {
        return assetType==AssetType.VIDEO;
    }
}
