package com.vsnt.asset_onboarding.config;

import com.vsnt.asset_onboarding.dtos.kvstore.segments.KVSegment;
import com.vsnt.asset_onboarding.entities.Media;
import com.vsnt.asset_onboarding.entities.enums.MediaType;
import com.vsnt.asset_onboarding.strategies.delivery.DeliverySecurityConfig;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@ConditionalOnProperty(
        name = "app.delivery.security.enabled",
        value = "false",
        matchIfMissing = true
)
@Component
public class DefaultDeliverySecurityConfig implements DeliverySecurityConfig {
    /*Default Delivery Security Config
    * 1. No signed url and signed Cookie
    * 2.
    * */
    @Override
    public String getSegmentURL(KVSegment segment) {
        return segment.getUrl();
    }

    @Override
    public String getPlaylistURL(Media media) {
        return media.getVideoAsset().getCdnURL();
    }

    @Override
    public void populateResponse(ResponseEntity<?> responseEntity, HttpServletResponse response, Media media, Object content) {
        if(media.getMediaType().equals(MediaType.LIVE))
        {
            response.addHeader(HttpHeaders.CONTENT_TYPE,"application/vnd.apple.mpegurl");
        }
        else {
            if(!(content instanceof String url))
            {
               throw new IllegalArgumentException("Invalid content type");
            }
            response.addHeader(HttpHeaders.LOCATION,url);
            response.setStatus(302);
        }
    }
}
