package com.vsnt.asset_onboarding.config;

import com.vsnt.asset_onboarding.dtos.kvstore.segments.KVSegment;
import com.vsnt.asset_onboarding.entities.Media;
import com.vsnt.asset_onboarding.entities.MediaAccessToken;
import com.vsnt.asset_onboarding.entities.enums.MediaType;
import com.vsnt.asset_onboarding.repositories.MediaAccessTokenRepository;
import com.vsnt.asset_onboarding.strategies.delivery.DeliverySecurityConfig;
import com.vsnt.asset_onboarding.utils.JWTService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;

@ConditionalOnProperty(
        name = "app.delivery.security.enabled",
        havingValue = "false",
        matchIfMissing = true
)
@Component
public class DefaultDeliverySecurityConfig implements DeliverySecurityConfig {
    private final JWTService jwtService;
    private final MediaAccessTokenRepository mediaAccessTokenRepository;
    public DefaultDeliverySecurityConfig(JWTService jwtService, MediaAccessTokenRepository mediaAccessTokenRepository) {

        this.jwtService = jwtService;
        this.mediaAccessTokenRepository = mediaAccessTokenRepository;
    }

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
    public boolean validateToken(String token, String assetId) {
        return true;
    }

    @Override
    public ResponseEntity<?> populateResponse( HttpServletResponse response, Media media, Object content) {

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
            response.addHeader(HttpHeaders.CACHE_CONTROL,"no-cache");
            response.setStatus(302);
        }
        if(media.getMediaType().equals(MediaType.LIVE))
        {
            return ResponseEntity.ok().body(content);
        }
        else {
            HashMap<String,String> res = new HashMap<>();
            res.put("url",(String)content);
            return ResponseEntity.ok().body(res);
        }
    }

    @Override
    public String[] generateTokens(String userId, String assetId) {

        String access_token = jwtService.generateToken(userId ,assetId ,10*60*1000L);
        MediaAccessToken token = new MediaAccessToken();
        token.setUserId(userId);
        token.setAssetId(assetId);
        token.setRefreshToken(jwtService.generateToken(userId,assetId,24*60*60* 1000L));
        token.setValidity(60*60* 1000L);
        token = mediaAccessTokenRepository.save(token);
        return new String[]{access_token,token.getRefreshToken()};
    }

    @Override
    public String refreshToken(HttpServletResponse httpServletResponse, String userId, String assetId, String token) {
        MediaAccessToken savedToken = mediaAccessTokenRepository.findByRefreshToken(token).orElse(null);
        if(savedToken == null ||savedToken.getCreatedAt().compareTo(Timestamp.from(Instant.now())) <= savedToken.getValidity()  )
        {
            throw new IllegalArgumentException("Invalid token");
        }
        if(jwtService.isTokenExpired(savedToken.getRefreshToken()))
        {
            throw new RuntimeException("Token is expired");
        }
        if(!savedToken.getUserId().equals(userId) || !savedToken.getAssetId().equals(assetId))
        {
            throw new IllegalArgumentException("Invalid token");
        }

        return jwtService.generateToken(userId ,assetId ,10*60*1000L);
    }
}
