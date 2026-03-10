package com.vsnt.asset_onboarding.strategies.delivery;

import com.vsnt.asset_onboarding.dtos.kvstore.segments.KVSegment;
import com.vsnt.asset_onboarding.entities.Media;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public interface DeliverySecurityConfig {
    String getSegmentURL(KVSegment segment);
    String getPlaylistURL(Media media);
    void populateResponse(ResponseEntity<?> responseEntity, HttpServletResponse response, Media media , Object content);
}
