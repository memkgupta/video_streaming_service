package com.vsnt.asset_onboarding.listeners;

import com.vsnt.asset_onboarding.MessageListener;
import com.vsnt.asset_onboarding.dtos.TranscodingSegmentUpdateDTO;
import com.vsnt.asset_onboarding.entities.Media;
import com.vsnt.asset_onboarding.services.MediaService;
import com.vsnt.asset_onboarding.services.SegmentService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SegmentMessageListener implements MessageListener<TranscodingSegmentUpdateDTO> {
    private final SegmentService segmentService;
    private final MediaService mediaService;
    private final SegmentUpdateHandlerFactory updateHandlerFactory;
    public SegmentMessageListener(SegmentService segmentService, MediaService mediaService, SegmentUpdateHandlerFactory updateHandlerFactory) {
        this.segmentService = segmentService;
        this.mediaService = mediaService;
        this.updateHandlerFactory = updateHandlerFactory;
    }

    @Override
    public void onMessage(TranscodingSegmentUpdateDTO message) {
        segmentService.save(message);
        handleUpdate(message);
    }
    @Async
    public void handleUpdate(TranscodingSegmentUpdateDTO updateRequestDTO) {
        try{
            Media media = mediaService.getMedia(UUID.fromString(updateRequestDTO.getMediaId()));
            updateHandlerFactory.getSegmentUpdateHandler(media.getMediaType())
                    .handle(updateRequestDTO, media);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
