package com.vsnt.asset_onboarding.services;

import com.vsnt.asset_onboarding.dtos.TranscodingSegmentUpdateDTO;
import com.vsnt.asset_onboarding.dtos.kvstore.segments.KVSegment;
import com.vsnt.asset_onboarding.entities.Media;
import com.vsnt.asset_onboarding.entities.TranscodedSegment;
import com.vsnt.asset_onboarding.entities.enums.MediaType;
import com.vsnt.asset_onboarding.exceptions.BadRequestException;
import com.vsnt.asset_onboarding.repositories.TranscodedSegmentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SegmentService {

    private final TranscodedSegmentRepository transcodedSegmentRepository;
    private final SegmentKVService segmentKVService;
    public SegmentService( TranscodedSegmentRepository transcodedSegmentRepository, SegmentKVService segmentKVService) {

        this.transcodedSegmentRepository = transcodedSegmentRepository;
        this.segmentKVService = segmentKVService;
    }

    public void save(TranscodingSegmentUpdateDTO segmentUpdateDTO) {
        TranscodedSegment segment = new TranscodedSegment();
        segment.setAssetId(segmentUpdateDTO.getAssetId());
        segment.setUrl(segmentUpdateDTO.getUrl());
        segment.setMediaId(segmentUpdateDTO.getMediaId());
        segment.setSequenceNumber(segmentUpdateDTO.getSequenceNumber());
        transcodedSegmentRepository.save(segment);
    }
    public String getLivePlaylist(
           Media media
    )
    {
        // todo return segments stored in redis kv store
        List<KVSegment> segments = segmentKVService.getLatestSegments(media.getId().toString());
        StringBuilder sb = new StringBuilder();

        sb.append("#EXTM3U\n");

// Required version (important)
        sb.append("#EXT-X-VERSION:3\n");

// Recommended for live streams
//        sb.append("#EXT-X-PLAYLIST-TYPE:EVENT\n");

// Must be >= max segment duration
        sb.append("#EXT-X-TARGETDURATION:5\n");

// Media sequence = first segment sequence number
        sb.append("#EXT-X-MEDIA-SEQUENCE:")
                .append(segments.get(0).getSequenceNumber())
                .append("\n");

// Optional but recommended (independent segments)
        sb.append("#EXT-X-INDEPENDENT-SEGMENTS\n");

// Add segments
        for (KVSegment segment : segments) {

            sb.append("#EXTINF:")
                    .append("4.0")
                    .append(",\n");

            sb.append(segment.getUrl()).append("\n");
        }
//sb.append("")
        return sb.toString();
    }
    public String getPlaylist(
            long start , Media media
    )  {

        if(media.getMediaType().equals(MediaType.STATIC))
        {
return getPlayListStatic(start , media);
        }
        else if(media.getMediaType().equals(MediaType.LIVE))
        {
       return getPlaylistLiveStream(start , media);
        }
    throw new BadRequestException("Invalid media type");
    }
    private String getPlayListStatic(Long start ,  Media media)
    {
        // serve the index file stored in the storage
        return media.getVideoAsset().getCdnURL();
    }
    private String getPlaylistLiveStream(long start , Media media)
    {
        long segmentStart = (long)Math.floor((double) start / 4000);
        long offset = start -segmentStart*4;
        long startTs = segmentStart*4;
        Pageable pageable = Pageable.ofSize(1000);
        Specification<TranscodedSegment> segment =
                Specification.allOf(
                        Specification.where(
                                (root,cq,cb)->
                                        cb.equal(
                                                root.get("assetId"),
                                                media.getVideoAsset().getId()
                                        )
                        ),
                        Specification.where(
                                (root,cq,cb)->
                                        cb.greaterThanOrEqualTo(
                                                root.get("start"),
                                                startTs
                                        )
                        )
                );
        //todo return segments from the db
        Page<TranscodedSegment> page = transcodedSegmentRepository
                .findAll(segment, pageable);
        List<TranscodedSegment> segments = page.getContent();
        if(segments.isEmpty()) {
            return getLivePlaylist(media);
        }
        StringBuilder sb = new StringBuilder();

        sb.append("#EXTM3U\n");
        sb.append("#EXT-X-VERSION:3\n");
        sb.append("##EXT-X-START:TIME-OFFSET=").append(offset).append("\n");
        sb.append("#EXT-X-TARGETDURATION:4\n");
        sb.append("#EXT-X-MEDIA-SEQUENCE:")

                .append(segments.get(0).getId().getSequenceNumber())
                .append("\n");
        sb.append("#EXT-X-INDEPENDENT-SEGMENTS\n");
        for(TranscodedSegment seg : segments) {
            sb.append("#EXTINF:4.0,\n");
            sb.append(seg.getUrl()).append("\n");
        }
        return sb.toString();
    }
}
