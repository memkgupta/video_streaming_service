package com.vsnt.asset_onboarding.strategies.playlist;

import com.vsnt.asset_onboarding.BitrateConfig;
import com.vsnt.asset_onboarding.CDNService;
import com.vsnt.asset_onboarding.config.Secrets;
import com.vsnt.asset_onboarding.entities.Media;
import com.vsnt.asset_onboarding.entities.TranscodedSegment;
import com.vsnt.asset_onboarding.repositories.TranscodedSegmentRepository;
import com.vsnt.asset_onboarding.services.S3Service;
import com.vsnt.asset_onboarding.services.SegmentService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;

@Component
public class SinglePlaylistGenerationStrategy implements PlaylistGenerationStrategy {
   private final CDNService cdnService;
   private final SegmentService segmentService;
   private final S3Service s3Service;
    private final TranscodedSegmentRepository transcodedSegmentRepository;
    public SinglePlaylistGenerationStrategy(CDNService cdnService, SegmentService segmentService, S3Service s3Service, TranscodedSegmentRepository transcodedSegmentRepository) {
        this.cdnService = cdnService;
        this.segmentService = segmentService;
        this.s3Service = s3Service;
        this.transcodedSegmentRepository = transcodedSegmentRepository;
    }

    @Override
    @Transactional
    public String  generate(Media media , BitrateConfig bitrateConfig) {
        StringBuilder sb = new StringBuilder();
        Long maxDuration = transcodedSegmentRepository
                .getMaxDuration(media.getId().toString() , bitrateConfig.getResolution()).orElse(null);
        if(maxDuration == null){
            maxDuration = 4000L;
            //todo replace with correct exception handling
//            throw new RuntimeException("Max duration is null");
        }
        long targetDuration = (long) Math.ceil(maxDuration / 1000.0);
        sb.append("#EXTM3U\n");
        sb.append("#EXT-X-VERSION:3\n\n");
        sb.append("#EXT-X-TARGETDURATION:")
                .append(targetDuration)
                .append("\n");

        sb.append("#EXT-X-MEDIA-SEQUENCE:0\n");
        sb.append("#EXT-X-PLAYLIST-TYPE:VOD\n\n");

        Stream<TranscodedSegment> segmentStream = transcodedSegmentRepository.findById_AssetIdAndId_ResolutionOrderById_SequenceNumber(
                media.getVideoAsset().getId().toString(),bitrateConfig.getResolution()
        );
        segmentStream.forEachOrdered(segment -> {

            sb.append("#EXTINF:")
                    .append(String.format("%.3f", segment.getDuration() / 1000.0))
                    .append(",\n");

            sb.append(segment.getUrl()).append("\n");
        });

        sb.append("\n#EXT-X-ENDLIST\n");
        String key = "transcoded/"+media.getVideoAsset().getId().toString()+"/playlists/"+bitrateConfig.getResolution().toString()+"/index.m3u8";
        String url = s3Service.uploadFileToS3(
                Secrets.AWS_SECURE_BUCKET,key , sb.toString().getBytes()

        );
        return url;

    }
}
