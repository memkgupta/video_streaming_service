package com.vsnt.asset_onboarding.services;

import com.vsnt.asset_onboarding.dtos.media.request.MediaCreateRequestDTO;
import com.vsnt.asset_onboarding.entities.Media;
import com.vsnt.asset_onboarding.entities.MediaPushKey;
import com.vsnt.asset_onboarding.entities.enums.MediaStatus;
import com.vsnt.asset_onboarding.entities.enums.MediaType;
import com.vsnt.asset_onboarding.exceptions.EntityNotFoundException;
import com.vsnt.asset_onboarding.producers.MediaBlockedProducer;
import com.vsnt.asset_onboarding.repositories.MediaPushKeyRepository;
import com.vsnt.asset_onboarding.repositories.MediaRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Service
public class MediaService {
    private final MediaRepository mediaRepository;

    private final MediaPushKeyRepository mediaPushKeyRepository;
    private final MediaBlockedProducer mediaBlockedProducer;
    public MediaService(MediaRepository mediaRepository, MediaPushKeyRepository mediaPushKeyRepository, MediaBlockedProducer mediaBlockedProducer) {
        this.mediaRepository = mediaRepository;

        this.mediaPushKeyRepository = mediaPushKeyRepository;
        this.mediaBlockedProducer = mediaBlockedProducer;
    }
    public Media save(Media media)
    {
        return mediaRepository.save(media);
    }
    public Media createMedia(MediaCreateRequestDTO request , String orgId)
    {
        MediaPushKey mediaPushKey = new MediaPushKey();
        mediaPushKey.setKey(UUID.randomUUID().toString());
        mediaPushKey.setActive(true);
        mediaPushKey = mediaPushKeyRepository.save(mediaPushKey);
        Media media = new Media();
        media.setCreatedAt(Timestamp.from(Instant.now()));
        media.setActive(true);
        media.setOrgId(orgId);
        media.setUpdatedAt(Timestamp.from(Instant.now()));
        media.setAccessibility(request.getMediaAccessibility());
        media.setMediaType(request.getMediaType());
        media.setPushKey(mediaPushKey);
        media.setStatus(MediaStatus.CREATED);
        media.setModerationEnabled(request.isModeration());
        return mediaRepository.save(media);
    }
    public Media getMediaByAsset(Long assetId)
    {
        return mediaRepository.findByVideoAsset_Id(assetId).orElse(null);
    }
//    @Cacheable(value = "mediaCache",key = "#id" )
    public Media getMedia(UUID id)
    {
        return mediaRepository.findById(id).orElse(null);
    }
    @Cacheable(value = "mediaTypeCache",key = "#id")
    public String getMediaType(UUID id)
    {
        Media media =  mediaRepository.findById(id).orElse(null);
        if(media == null)
        {
            throw new EntityNotFoundException("Media",id.toString());
        }
        return media.getMediaType().toString();
    }
    public void deleteMedia(UUID mediaId)
    {
        Media media = getMedia(mediaId);
        if(media == null)
        {
            throw new EntityNotFoundException("Media",mediaId.toString());
        }
        media.setActive(false);
        mediaRepository.save(media);
    }
    public Page<Media> getAllMedia(
            Specification<Media> specification , int page , int limit
    )
    {
        Pageable pageable = PageRequest.of(page - 1, limit);
        return mediaRepository.findAll(specification , pageable);
    }
    public void blockMedia(UUID mediaId , String reason)
    {
        Media media = getMedia(mediaId);
        media.setStatus(MediaStatus.BLOCKED);
        media.setActive(false);
        mediaBlockedProducer.send(mediaId.toString() , reason);
        mediaRepository.save(media);
    }

}
