package com.vsnt.videos_service.services;

import com.vsnt.videos_service.config.Secrets;
import com.vsnt.videos_service.entities.LiveStream;
import com.vsnt.videos_service.entities.LiveStreamSegment;
import com.vsnt.videos_service.repositories.LiveStreamSegmentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class PlaylistService {
    static class Pair{
        Future<String> url;
        String key;
        public Pair(Future<String> url, String key){
            this.url = url;
            this.key = key;
        }
    }
private final LiveStreamSegmentRepository liveStreamSegmentRepository;
private final S3Service s3Service;
    public PlaylistService(LiveStreamSegmentRepository liveStreamSegmentRepository, S3Service s3Service) {
        this.liveStreamSegmentRepository = liveStreamSegmentRepository;
        this.s3Service = s3Service;
    }
    @Async
    public CompletableFuture<String> generateFinalPlaylist(
            LiveStream stream
    )
    {
        long totalSegments =  liveStreamSegmentRepository.countByLiveStreamId(
                stream.getId().toString()
        );
        long chunks = totalSegments/1000;
        String prefix = "streams/"+stream.getStreamKey();
        List<Pair> chunkList = new ArrayList<>();
        ExecutorService threadPool = Executors.newFixedThreadPool(5);
        for(int i = 0; i < chunks; i++) {
            String key = prefix +"/chunks/"+ i+".m3u8";
            Pageable pageable = PageRequest.of(i,1000);
            Future<String> chunkUrl = threadPool.submit(() -> {
                Page<LiveStreamSegment> page =
                        liveStreamSegmentRepository.findByLiveStreamIdOrderById_NumberAsc(
                                stream.getId().toString(),
                        pageable
                        );
                List<LiveStreamSegment> segments = page.getContent();
                return writeChunk(key, segments);
            });
            chunkList.add(new Pair(chunkUrl,key));
        }
        StringBuilder sb = new StringBuilder();
        sb.append("#EXTM3U\n");
        sb.append("#EXT-X-VERSION:3\n\n");
        for(Pair p : chunkList){
            sb.append(p.url);
            sb.append("\n");
        }
        String masterKey = prefix+"/index.m3u8";
         s3Service.uploadPlaylist(
                prefix+"/index.m3u8",sb.toString()
        );
         return CompletableFuture.completedFuture(Secrets.CDN_URL_NAME + "/" + masterKey);
    }
    private String writeChunk(String key,
                              List<LiveStreamSegment> segments) {

        StringBuilder sb = new StringBuilder(4096);

        // HLS playlist header
        sb.append("#EXTM3U\n");
        sb.append("#EXT-X-VERSION:3\n");

        // Calculate target duration (max segment duration rounded up)
        int targetDuration = segments.stream()
                .mapToInt(segment -> (int) Math.ceil(segment.getDuration()))
                .max()
                .orElse(4);

        sb.append("#EXT-X-TARGETDURATION:")
                .append(targetDuration)
                .append("\n");

        // Media sequence = first segment number
        long firstSegmentNumber =
                segments.isEmpty()
                        ? 0
                        : segments.get(0).getId().getNumber();

        sb.append("#EXT-X-MEDIA-SEQUENCE:")
                .append(firstSegmentNumber)
                .append("\n\n");

        // Add each segment
        for (LiveStreamSegment segment : segments) {

            sb.append("#EXTINF:")
                    .append(segment.getDuration())
                    .append(",\n");

            sb.append(segment.getUrl())
                    .append("\n");
        }

        // Mark playlist complete (VOD chunk)
        sb.append("\n#EXT-X-ENDLIST\n");

        String content =  sb.toString();
        s3Service.uploadPlaylist(key,content);
        String url = Secrets.CDN_URL_NAME+"/"+key;
    return url;
    }

}
