package com.vsnt.asset_onboarding.config;

import com.vsnt.asset_onboarding.CDNService;
import com.vsnt.asset_onboarding.dtos.kvstore.segments.KVSegment;
import com.vsnt.asset_onboarding.dtos.security.SignedCookie;
import com.vsnt.asset_onboarding.entities.Media;
import com.vsnt.asset_onboarding.entities.MediaAccessToken;
import com.vsnt.asset_onboarding.entities.enums.MediaType;
import com.vsnt.asset_onboarding.repositories.MediaAccessTokenRepository;
import com.vsnt.asset_onboarding.services.CloudFrontService;
import com.vsnt.asset_onboarding.strategies.delivery.DeliverySecurityConfig;
import com.vsnt.asset_onboarding.utils.JWTService;
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
    private final MediaAccessTokenRepository mediaAccessTokenRepository;
    private final String DOMAIN_NAME = "${app.domain.name}";
    private final JWTService jWTService;

    public SignedCookieDeliverySecurityConfig(CloudFrontService cloudFrontService, MediaAccessTokenRepository mediaAccessTokenRepository, JWTService jWTService) {
        this.cdnService = cloudFrontService;
        this.mediaAccessTokenRepository = mediaAccessTokenRepository;
        this.jWTService = jWTService;
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
    public boolean validateToken(String token, String assetId) {
        if(jWTService.isTokenExpired(token)) {
            throw new RuntimeException("Token is expired");
        }
        String extractedAssetId = jWTService.extractAssetId(token);
        return extractedAssetId.equals(assetId);
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

    @Override
    public String[] generateTokens(String userId, String assetId) {
        String access_token = jWTService.generateToken(userId ,assetId ,10*60*1000L);
        MediaAccessToken token = new MediaAccessToken();
        token.setUserId(userId);
        token.setAssetId(assetId);
        token.setRefreshToken(jWTService.generateToken(userId,assetId,24*60*60* 1000L));
        token.setValidity(60*60* 1000L);
        token = mediaAccessTokenRepository.save(token);
       return new String[]{access_token,token.getRefreshToken()};
    }

    @Override
    public String refreshToken(HttpServletResponse httpServletResponse, String userId, String assetId, String refreshToken) {
        MediaAccessToken savedToken = mediaAccessTokenRepository.findByRefreshToken(refreshToken).orElse(null);
        if(savedToken == null)
        {
            throw new IllegalArgumentException("Invalid token");
        }
        if(jWTService.isTokenExpired(savedToken.getRefreshToken()))
        {
            throw new RuntimeException("Token is expired");
        }
        if(!savedToken.getUserId().equals(userId) || !savedToken.getAssetId().equals(assetId))
        {
            throw new IllegalArgumentException("Invalid token");
        }

        return jWTService.generateToken(userId ,assetId ,10*60*1000L);
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
