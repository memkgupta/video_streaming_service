package com.vsnt.asset_onboarding.services;

import com.vsnt.asset_onboarding.entities.Media;
import com.vsnt.asset_onboarding.entities.enums.AssetType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class WatchService {
    private final SegmentService segmentService;

    public WatchService(SegmentService segmentService) {
        this.segmentService = segmentService;
    }

    public String watch(Media media ,Long start)
{
    String indexFile = null;
    if(media.getVideoAsset().getAssetType().equals(AssetType.LIVE_VIDEO))
    {

        if(start<0)
        {
            // get live playlist
            indexFile= segmentService.getLiveMasterPlaylist(media);
        }
        else {
            // get playlist with offset
            indexFile = segmentService.getLiveMasterPlaylist(media,start);
        }

    }
    else if(media.getVideoAsset().getAssetType().equals(AssetType.VIDEO)){
         indexFile = segmentService.getPlaylist(-1, media);
    }
    else {
        throw new RuntimeException("Unsupported Media");
    }
    return indexFile;
}
public String watchLiveVariant(Media media ,String resolution ,Long start)
{
    String indexFile = "";
    if(start<0)
    {
        indexFile=  segmentService.getLiveVariantPlaylist(media,resolution);

    }
    else {
        indexFile = segmentService.getLiveVariantPlaylist(media,resolution,start);
    }
    return indexFile;
}

}
