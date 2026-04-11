package com.vsnt.asset_onboarding.entities.enums;

public enum MediaStatus {
    CREATED , // when media metadata is created
    PROCESSING, // for VOD only when video is being transcoded
    READY, // for VOD only when video is ready to stream
    LIVE, // for live video only when video is live
    ENDED, // for live vide only once live stream ends
    FAILED, // for both live and vod when the transcoding or any stage fails even after retries
    BLOCKED // for both live and vod when the media is blocked
}
