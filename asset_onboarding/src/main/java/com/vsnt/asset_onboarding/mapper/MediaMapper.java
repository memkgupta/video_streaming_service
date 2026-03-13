package com.vsnt.asset_onboarding.mapper;

import com.vsnt.asset_onboarding.dtos.media.response.MediaDTO;
import com.vsnt.asset_onboarding.entities.Media;
import org.springframework.stereotype.Component;

@Component
public class MediaMapper {


    public MediaDTO toMediaDTO(Media media){
    MediaDTO mediaDTO  =  MediaDTO.builder()
            .id(media.getId())
            .pushKey(media.getPushKey().getKey())
            .createdAt(media.getCreatedAt())
            .updatedAt(media.getUpdatedAt())
            .active(media.isActive())
            .accessibility(media.getAccessibility())
            .mediaType(media.getMediaType())
            .status(media.getStatus())
            .build();
    return mediaDTO;
}

}
