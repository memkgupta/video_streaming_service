package com.vsnt.videos_service.services;

import com.vsnt.videos_service.communication.AssetServiceCommunication;
import com.vsnt.videos_service.dtos.RedisSegmentDTO;
import com.vsnt.videos_service.dtos.StreamSegmentUpdateDTO;
import com.vsnt.videos_service.entities.*;
import com.vsnt.videos_service.repositories.LiveStreamRepository;
import com.vsnt.videos_service.repositories.LiveStreamSegmentRepository;
import com.vsnt.videos_service.repositories.VideoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;


import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Service
public class LiveStreamService {
    private final LiveStreamRepository liveStreamRepository;
    private final LiveStreamSegmentRepository liveStreamSegmentRepository;
    private final StreamSegmentKVService  streamSegmentKVService;
    private final PlaylistService playlistService;
    private final AssetServiceCommunication assetServiceCommunication;
   private final VideoRepository videoRepository;
    public LiveStreamService(LiveStreamRepository liveStreamRepository, LiveStreamSegmentRepository liveStreamSegmentRepository, StreamSegmentKVService streamSegmentKVService, PlaylistService playlistService, VideoRepository videoRepository , AssetServiceCommunication assetServiceCommunication) {
        this.liveStreamRepository = liveStreamRepository;
        this.liveStreamSegmentRepository = liveStreamSegmentRepository;
        this.streamSegmentKVService = streamSegmentKVService;
        this.videoRepository = videoRepository;
        this.assetServiceCommunication = assetServiceCommunication;
        this.playlistService = playlistService;

    }
    public LiveStream createLiveStream(String channelId)
    {
        LiveStream liveStream = new LiveStream();
        liveStream.setChannelId(channelId);
        liveStream.setStartTime(Timestamp.from(Instant.now()));
//        liveStream.setEndTime(Timestamp.from(Instant.now()));
        liveStream.setStreamKey(UUID.randomUUID().toString());
        liveStream.setLive(true);
        return  liveStreamRepository.save(liveStream);
    }
    public LiveStream getLiveStream(UUID liveStreamId) {
        return liveStreamRepository.findById(liveStreamId).orElse(null);

    }
    public  LiveStream getLiveStreamByKey(String liveStreamKey) {
    return liveStreamRepository.findByStreamKey(liveStreamKey).orElse(null);
    }
    public void updateSegement(StreamSegmentUpdateDTO  streamSegmentUpdateDTO) {
        LiveStream stream = this.getLiveStreamByKey(
                streamSegmentUpdateDTO.getStreamKey()
        );
        if(stream == null) {
            throw new RuntimeException("Stream not found");
        }
        streamSegmentKVService.addSegment(
                RedisSegmentDTO.builder()
                        .url(streamSegmentUpdateDTO.getUrl())
                        .segmentId(streamSegmentUpdateDTO.getSegmentId())
                        .streamKey(streamSegmentUpdateDTO.getStreamKey())
                        .build()
        );
        LiveStreamSegment segment = new LiveStreamSegment();
        segment.setId(
                SegmentId.builder()
                        .number(streamSegmentUpdateDTO.getSegmentId())
                        .streamKey(streamSegmentUpdateDTO.getStreamKey())
                        .build()
        );
        segment.setStartDuration(
                streamSegmentUpdateDTO.getStart()
        );
        segment.setUrl(streamSegmentUpdateDTO.getUrl());
        segment.setEndDuration(
                streamSegmentUpdateDTO.getEnd()
        );
                segment.setLiveStreamId(stream.getId().toString());
                segment.setCreatedAt(
                        Timestamp.from(Instant.now())
                );
        liveStreamSegmentRepository.save(segment);


    }
    public void endStream(
            String streamKey
    )
    {
        LiveStream liveStream = this.getLiveStreamByKey(streamKey);
        if(liveStream == null) {
            throw new RuntimeException("Stream not found");
        }
        if(!liveStream.isLive())
        {
            throw new RuntimeException("Live stream is not live");
        }
        liveStream.setEndTime(Timestamp.from(Instant.now()));
        liveStream.setLive(false);
        streamSegmentKVService.clear(liveStream.getStreamKey());
        //todo worker initiate the merger process

        liveStreamRepository.save(liveStream);
        convertToVOD(liveStream);
    }
    public String getLivePlaylist(
            String streamKey
    )
    {
        // todo return segments stored in redis kv store
        List<RedisSegmentDTO> segments = streamSegmentKVService.getLatestSegments(streamKey);
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
                .append(segments.get(0).getSegmentId())
                .append("\n");

// Optional but recommended (independent segments)
        sb.append("#EXT-X-INDEPENDENT-SEGMENTS\n");

// Add segments
        for (RedisSegmentDTO segment : segments) {

            sb.append("#EXTINF:")
                    .append("4.0")
                    .append(",\n");

            sb.append(segment.getUrl()).append("\n");
        }
//sb.append("")
        return sb.toString();
    }
    public String getPlaylist(
            Long start , String streamKey
    )
    {
        LiveStream liveStream = this.getLiveStreamByKey(streamKey);
        if(liveStream == null) {
            throw new RuntimeException("Stream not found");
        }
        long segmentStart = (long)Math.floor((double) start / 4000);
        Long offset = start -segmentStart*4;
       long startTs = segmentStart*4;
        Pageable pageable = Pageable.ofSize(10);
        Specification<LiveStreamSegment> segment =
                Specification.allOf(
                        Specification.where(
                                (root,cq,cb)->
                                        cb.equal(
                                                root.get("liveStreamId"),
                                                liveStream.getId()
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
        Page<LiveStreamSegment> page = liveStreamSegmentRepository
                .findAll(segment, pageable);
        List<LiveStreamSegment> segments = page.getContent();
        if(segments.isEmpty()) {
            return getLivePlaylist(streamKey);
        }
        StringBuilder sb = new StringBuilder();

        sb.append("#EXTM3U\n");
        sb.append("#EXT-X-VERSION:3\n");
        sb.append("##EXT-X-START:TIME-OFFSET="+offset+"\n");
        sb.append("#EXT-X-TARGETDURATION:4\n");
        sb.append("#EXT-X-MEDIA-SEQUENCE:")

                .append(segments.get(0).getId().getNumber())
                .append("\n");
        sb.append("#EXT-X-INDEPENDENT-SEGMENTS\n");
        for(LiveStreamSegment seg : segments) {
            sb.append("#EXTINF:4.0,\n");
            sb.append(seg.getUrl()).append("\n");
        }
        return sb.toString();
    }
    public void convertToVOD(LiveStream live)
    {
        CompletableFuture<String> future
         = playlistService.generateFinalPlaylist(live);
    CompletableFuture<Video> fV =    future.thenApply(s -> {
            String assetId = assetServiceCommunication.createAsset(s);
            Video video = new Video();
            video.setAssetId(assetId);
            video.setStatus(VideoUploadStatusEnum.COMPLETED);
            video.setChannelId(live.getChannelId());
            return  videoRepository.save(video);
        });
    }
}
