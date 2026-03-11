package com.vsnt.asset_onboarding.services;

import com.vsnt.asset_onboarding.dtos.TranscodingSegmentUpdateDTO;
import com.vsnt.asset_onboarding.dtos.kvstore.segments.KVSegment;
import com.vsnt.asset_onboarding.entities.Media;
import com.vsnt.asset_onboarding.entities.TranscodedSegment;
import com.vsnt.asset_onboarding.entities.TranscodedSegmentId;

import com.vsnt.asset_onboarding.entities.enums.ResolutionEnum;
import com.vsnt.asset_onboarding.repositories.TranscodedSegmentRepository;
import com.vsnt.asset_onboarding.strategies.delivery.DeliverySecurityConfig;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SegmentService {
    private final String[] resolutions = new String[]{"360p","480p","720p","1080p"};
    private final TranscodedSegmentRepository transcodedSegmentRepository;
    private final SegmentKVService segmentKVService;
    private final DeliverySecurityConfig deliverySecurityConfig;
    public SegmentService(TranscodedSegmentRepository transcodedSegmentRepository, SegmentKVService segmentKVService, DeliverySecurityConfig deliverySecurityConfig) {
        this.transcodedSegmentRepository = transcodedSegmentRepository;
        this.segmentKVService = segmentKVService;
        this.deliverySecurityConfig = deliverySecurityConfig;
    }
    public void save(TranscodingSegmentUpdateDTO segmentUpdateDTO) {
        TranscodedSegment segment = new TranscodedSegment();
        segment.setId(new TranscodedSegmentId(segmentUpdateDTO.getAssetId() ,  segmentUpdateDTO.getSequenceNumber(),segmentUpdateDTO.getResolution()));
        segment.setUrl(segmentUpdateDTO.getUrl());
        segment.setMediaId(segmentUpdateDTO.getMediaId());
        segment.setDuration(segmentUpdateDTO.getDuration());
        transcodedSegmentRepository.save(segment);
    }
    public String getLiveVariantPlaylist(Media media, String resolution) {

        List<KVSegment> segments =
                segmentKVService.getLatestSegments(
                        media.getVideoAsset().getId().toString(),
                        resolution
                );

        StringBuilder sb = new StringBuilder();

        sb.append("#EXTM3U\n");
        sb.append("#EXT-X-VERSION:3\n");
        sb.append("#EXT-X-TARGETDURATION:5\n");
        sb.append("#EXT-X-MEDIA-SEQUENCE:")
                .append(segments.get(0).getSequenceNumber())
                .append("\n");
        sb.append("#EXT-X-INDEPENDENT-SEGMENTS\n");

        for (KVSegment segment : segments) {
            sb.append("#EXTINF:4.0,\n");
            sb.append(deliverySecurityConfig.getSegmentURL(segment)).append("\n");
        }

        return sb.toString();
    }
    public String getLiveVariantPlaylist(Media media,String resolution, Long start) {
        // todo fetch segment from db

        Specification<TranscodedSegment> specification =
                Specification.allOf(
                        Specification.where(((root, query, criteriaBuilder) ->
                        {
                            return   criteriaBuilder.and(
                                    criteriaBuilder.equal(root.get("media").get("id"),media.getId()),
                                    criteriaBuilder.equal(root.get("id").get("resolution"), ResolutionEnum.valueOf("RESOLUTION_"+resolution.toUpperCase()))
                            );
                        })),
                        Specification.where(((root, query, criteriaBuilder) ->
                        {
                           return criteriaBuilder.greaterThanOrEqualTo(root.get("start"), start);
                        }))
                );

        List<KVSegment> segments =
                transcodedSegmentRepository.getTranscodedSegments(specification).stream().map(ts->KVSegment.builder()
                        .url(ts.getUrl())
                        .resolution(ts.getId().getResolution().toResolutionString())
                        .assetId(ts.getId().getAssetId())
                        .sequenceNumber(ts.getId().getSequenceNumber())
                        .duration(ts.getDuration())
                        .build()
                ).toList();

        StringBuilder sb = new StringBuilder();
        sb.append("#EXTM3U\n");
        sb.append("#EXT-X-VERSION:3\n");
        sb.append("#EXT-X-TARGETDURATION:5\n");
        sb.append("#EXT-X-MEDIA-SEQUENCE:")
                .append(segments.get(0).getSequenceNumber())
                .append("\n");
        sb.append("#EXT-X-INDEPENDENT-SEGMENTS\n");

        for (KVSegment segment : segments) {
            sb.append("#EXTINF:4.0,\n");
            sb.append(deliverySecurityConfig.getSegmentURL(segment)).append("\n");
        }

        return sb.toString();
    }
    public String getLiveMasterPlaylist(Media media) {

        StringBuilder sb = new StringBuilder();

        sb.append("#EXTM3U\n");
        sb.append("#EXT-X-VERSION:3\n\n");

        // bitrate + resolution mapping
        Map<String, String[]> streamInfo = Map.of(
                "360p", new String[]{"800000", "640x360"},
                "480p", new String[]{"1400000", "842x480"},
                "720p", new String[]{"2800000", "1280x720"},
                "1080p", new String[]{"5000000", "1920x1080"}
        );

        for (String resolution : resolutions) {

            String[] info = streamInfo.getOrDefault(
                    resolution,
                    new String[]{"1000000", "1280x720"}
            );

            String bandwidth = info[0];
            String res = info[1];

            sb.append("#EXT-X-STREAM-INF:BANDWIDTH=")
                    .append(bandwidth)
                    .append(",RESOLUTION=")
                    .append(res)
                    .append("\n");

            // URL to variant playlist endpoint
            sb.append("http://localhost:8081/watch/live/")
                    .append(media.getId())
                    .append("/")
                    .append(resolution)
                    .append("/playlist.m3u8\n\n");
        }

        return sb.toString();
    }
    public String getLiveMasterPlaylist(Media media,Long start) {

        StringBuilder sb = new StringBuilder();

        sb.append("#EXTM3U\n");
        sb.append("#EXT-X-VERSION:3\n\n");

        // bitrate + resolution mapping
        Map<String, String[]> streamInfo = Map.of(
                "360p", new String[]{"800000", "640x360"},
                "480p", new String[]{"1400000", "842x480"},
                "720p", new String[]{"2800000", "1280x720"},
                "1080p", new String[]{"5000000", "1920x1080"}
        );

        for (String resolution : resolutions) {

            String[] info = streamInfo.getOrDefault(
                    resolution,
                    new String[]{"1000000", "1280x720"}
            );

            String bandwidth = info[0];
            String res = info[1];

            sb.append("#EXT-X-STREAM-INF:BANDWIDTH=")
                    .append(bandwidth)
                    .append(",RESOLUTION=")
                    .append(res)
                    .append("\n");

            // URL to variant playlist endpoint
            sb.append("http://localhost:8081/watch/live/")
                    .append(media.getId())
                    .append("/")
                    .append(resolution)
                    .append("/playlist.m3u8")
                    .append("?start=")
                    .append(start)
                    .append("\n\n");
        }

        return sb.toString();
    }

    public String getPlaylist(
            long start , Media media
    )  {
return getPlayListStatic(start , media);
    }
    private String getPlayListStatic(Long start ,  Media media)
    {
        // serve the index file stored in the storage
        return deliverySecurityConfig.getPlaylistURL(media);
    }

}
