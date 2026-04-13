package com.vsnt.common_lib.dtos.events.media.review;

import com.vsnt.common_lib.dtos.events.media.MediaEvent;
import com.vsnt.common_lib.dtos.events.media.MediaEventType;

import java.time.Instant;

public class MediaUnderReviewEvent extends MediaEvent<MediaReviewPayload> {
    public MediaUnderReviewEvent( String mediaId, Instant timestamp,String orgId,MediaReviewPayload data) {
        super(MediaEventType.MEDIA_UNDER_REVIEW, mediaId, timestamp , orgId,data);
    }
}
