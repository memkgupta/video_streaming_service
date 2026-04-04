package com.vsnt.asset_onboarding.listeners;

import com.vsnt.asset_onboarding.MessageListener;
import com.vsnt.asset_onboarding.dtos.TranscodingSegmentUpdateDTO;
import com.vsnt.asset_onboarding.entities.Media;
import com.vsnt.asset_onboarding.entities.enums.MediaType;
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
        handleUpdate(message);
        saveAsync(message);

    }


    @Async
    public void saveAsync(TranscodingSegmentUpdateDTO message) {
        segmentService.save(message);
    }
    @Async
    public void handleUpdate(TranscodingSegmentUpdateDTO updateRequestDTO) {
        try{
            MediaType mediaType = MediaType.valueOf(mediaService.getMediaType(UUID.fromString(updateRequestDTO.getMediaId())));
            updateHandlerFactory.getSegmentUpdateHandler(mediaType)
                    .handle(updateRequestDTO);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
