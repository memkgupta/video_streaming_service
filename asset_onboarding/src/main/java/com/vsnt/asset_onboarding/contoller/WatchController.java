package com.vsnt.asset_onboarding.contoller;

import com.vsnt.asset_onboarding.dtos.security.SignedCookie;
import com.vsnt.asset_onboarding.entities.Media;
import com.vsnt.asset_onboarding.entities.enums.MediaAccessibility;
import com.vsnt.asset_onboarding.entities.enums.MediaStatus;
import com.vsnt.asset_onboarding.entities.enums.MediaType;
import com.vsnt.asset_onboarding.exceptions.EntityNotFoundException;
import com.vsnt.asset_onboarding.services.GroupMemberService;
import com.vsnt.asset_onboarding.services.MediaService;
import com.vsnt.asset_onboarding.services.SegmentService;
import com.vsnt.asset_onboarding.utils.CookiesService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.cloudfront.cookie.CookiesForCannedPolicy;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/watch")
public class WatchController {
    private final MediaService mediaService;
    private final GroupMemberService groupMemberService;
    private final SegmentService segmentService;
    private final CookiesService cookiesService;
    public WatchController(MediaService mediaService, GroupMemberService groupMemberService, SegmentService segmentService, CookiesService cookiesService) {
        this.mediaService = mediaService;
        this.groupMemberService = groupMemberService;
        this.segmentService = segmentService;
        this.cookiesService = cookiesService;
    }

    @GetMapping("/{mediaId}")
    public ResponseEntity<?> watch(@PathVariable  UUID mediaId , @RequestHeader Map<String, String> headers , @RequestParam(
            defaultValue = "-1"
    ) long start , HttpServletResponse httpServletResponse)
    {
        Media media = mediaService.getMedia(mediaId);
        ResponseEntity<?> responseEntity;
        if(media == null || !(media.getStatus().equals(MediaStatus.READY) || media.getStatus().equals(MediaStatus.LIVE)))
        {
            throw new EntityNotFoundException("Media");
        }

        boolean allowed = isAllowedToWatch(
                media , headers
        );
        if(!allowed)
        {
            throw new RuntimeException("Forbidden");
        }
      if(media.getMediaType().equals(MediaType.LIVE))
      {
          String indexFile = null;
          if(start<0)
          {
              // get live playlist
            indexFile= segmentService.getLivePlaylist(media);

          }
          else {
              // get playlist with offset
              indexFile = segmentService.getPlaylist(start, media);
          }
          responseEntity = ResponseEntity.ok()
                  .header(HttpHeaders.CONTENT_TYPE, "application/vnd.apple.mpegurl")
                  .body(indexFile);
      }
      else {
           String indexFile =  segmentService.getPlaylist(-1 , media);

           responseEntity = ResponseEntity.status(302)
                   .header(HttpHeaders.LOCATION,indexFile).build();
      }
        SignedCookie cookies = cookiesService.generateCookies();
        addCookie(httpServletResponse, "CloudFront-Key-Pair-Id", cookies.getKeyPairId());
        addCookie(httpServletResponse, "CloudFront-Expires", cookies.getExpires());
        addCookie(httpServletResponse, "CloudFront-Signature", cookies.getSignature());
      return responseEntity;
    }
    private boolean isAllowedToWatch(Media media , Map<String, String> headers)
    {
        if(media.getAccessibility().equals(MediaAccessibility.PUBLIC))
        {
           return true;
        }
        else if(media.getAccessibility().equals(MediaAccessibility.PRIVATE))
        {
            String pullKey = headers.get("x-pull-key");
            //todo validate from security the pull key and media
            if(pullKey != null )
            {
              return true;
            }
        }
        else {
            String userId = headers.get("x-user-id");
            if(userId!=null )
            {
                //todo check for group membership status
               return groupMemberService.isGroupMember(
                        userId , media.getGroup()
                );
            }
        }
        return false;
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

        response.addCookie(cookie);
    }
}
