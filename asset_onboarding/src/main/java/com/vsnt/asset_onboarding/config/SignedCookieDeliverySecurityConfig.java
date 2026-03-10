package com.vsnt.asset_onboarding.config;

import com.vsnt.asset_onboarding.CDNService;
import com.vsnt.asset_onboarding.dtos.kvstore.segments.KVSegment;
import com.vsnt.asset_onboarding.dtos.security.SignedCookie;
import com.vsnt.asset_onboarding.entities.Media;
import com.vsnt.asset_onboarding.entities.enums.MediaType;
import com.vsnt.asset_onboarding.services.CloudFrontService;
import com.vsnt.asset_onboarding.strategies.delivery.DeliverySecurityConfig;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@ConditionalOnExpression(
        value =    "'${app.delivery.security.enabled:false}' == 'true' && " +
                "'${app.delivery.security.type:}' == 'cookie'"
)
@Component
public class SignedCookieDeliverySecurityConfig implements DeliverySecurityConfig {
    private final CDNService cdnService;
    private final String DOMAIN_NAME = "${app.domain.name}";
    public SignedCookieDeliverySecurityConfig(CloudFrontService cloudFrontService) {
        this.cdnService = cloudFrontService;
    }

    @Override
    public String getSegmentURL(KVSegment segment) {
        /*If this is the configuration than it is already assumed that the segment
        * stored is in the secured distribution
        * */
        return segment.getUrl();
    }

    @Override
    public String getPlaylistURL(Media media) {
        /*If this is the configuration than it is already assumed that the segment
         * stored is in the secured distribution
         * */
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
        /*Need to generate the signed cookie and set it in the response */
        SignedCookie cookies = cdnService.generateCookies();
        addCookie(response, "CloudFront-Key-Pair-Id", cookies.getKeyPairId());
        addCookie(response, "CloudFront-Expires", cookies.getExpires());
        addCookie(response, "CloudFront-Signature", cookies.getSignature());
    }
    private void addCookie(
            HttpServletResponse response,
            String name,
            String value
    ) {
        Cookie cookie = new Cookie(name, value);

        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setDomain(DOMAIN_NAME);
        response.addCookie(cookie);
    }
}
