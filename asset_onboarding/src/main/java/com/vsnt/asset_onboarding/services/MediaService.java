package com.vsnt.asset_onboarding.services;

import com.vsnt.asset_onboarding.dtos.media.request.MediaCreateRequestDTO;
import com.vsnt.asset_onboarding.entities.Group;
import com.vsnt.asset_onboarding.entities.Media;
import com.vsnt.asset_onboarding.entities.MediaPushKey;
import com.vsnt.asset_onboarding.repositories.MediaPushKeyRepository;
import com.vsnt.asset_onboarding.repositories.MediaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class MediaService {
    private final MediaRepository mediaRepository;
    private final GroupService groupService;
    private final MediaPushKeyRepository mediaPushKeyRepository;
    public MediaService(MediaRepository mediaRepository, GroupService groupService, MediaPushKeyRepository mediaPushKeyRepository) {
        this.mediaRepository = mediaRepository;
        this.groupService = groupService;
        this.mediaPushKeyRepository = mediaPushKeyRepository;
    }
    public Media createMedia(MediaCreateRequestDTO request)
    {
        MediaPushKey mediaPushKey = new MediaPushKey();
        mediaPushKey.setKey(UUID.randomUUID().toString());
        mediaPushKey = mediaPushKeyRepository.save(mediaPushKey);
        Media media = new Media();
        media.setCreatedAt(Timestamp.from(Instant.now()));
        media.setUpdatedAt(Timestamp.from(Instant.now()));
        media.setAccessibility(request.getMediaAccessibility());
        media.setMediaType(request.getMediaType());
        media.setPushKey(mediaPushKey);
        media.setModerationEnabled(request.isModeration());
        if(request.getGroupId() != null)
        {

           Group group = groupService.getGroup(UUID.fromString(request.getGroupId()));
           if(group == null)
           {
               throw new EntityNotFoundException("Group");
           }
           media.setGroup(group);
        }
        return mediaRepository.save(media);
    }
    public Media getMedia(UUID id)
    {
        return mediaRepository.findById(id).orElse(null);
    }
    public void deleteMedia(UUID mediaId)
    {
        Media media = getMedia(mediaId);
        if(media == null)
        {
            throw new EntityNotFoundException("Media");
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

}
