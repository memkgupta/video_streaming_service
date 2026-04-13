package com.vsnt;

import com.vsnt.dtos.MediaType;
import com.vsnt.dtos.TranscodingFinishEventDTO;
import com.vsnt.dtos.UpdateRequestDTO;
import com.vsnt.services.APIService;
import com.vsnt.services.HlsDirectoryWatcher;

import com.vsnt.services.S3Service;
import org.apache.kafka.common.protocol.types.Field;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.concurrent.Future;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        S3Service s3Service = new S3Service();
        String file_key = System.getenv("FILE_KEY");
        String mediaId = System.getenv("MEDIA_ID");
        boolean moderation = System.getenv("MODERATION").equals("true");
        String encryptionKey = System.getenv("ENCRYPTION_KEY");
        MediaType mediaType = MediaType.valueOf(System.getenv("MEDIA_TYPE"));
        String bucket_name = System.getenv("BUCKET_NAME");
        String transcoded_bucket_name = System.getenv("TRANSCODED_BUCKET_NAME");
        String kafka_brokers = System.getenv("KAFKA_BROKERS");
        String kafka_topic_segment_update = System.getenv("UPDATE_TOPIC_NAME");
        String kafka_topic_finish = System.getenv("FINISH_TOPIC_NAME");
        String assetId =  System.getenv("ASSET_ID");
        String publicKeyURL = System.getenv("PUBLIC_KEY_URL");
        String cloudFrontURL = System.getenv("CLOUDFRONT_URL");
        if(file_key == null || bucket_name == null){
            System.out.println("Missing environment variables");
            System.exit(1);
        }

        try {
            String signedURL ="";
            if(mediaType.equals(MediaType.LIVE))
            {
                signedURL = file_key;
            }
            else {
                signedURL =  s3Service.generatePresignedUrl(bucket_name , file_key);
            }
            VideoTranscoder transcoder = new VideoTranscoder();
SegmentEventProducer producer = new SegmentEventProducer(kafka_brokers,
        kafka_topic_segment_update,kafka_topic_finish
        );
ModerationJobProducer moderationJobProducer = new ModerationJobProducer(

);
            String[] resolutions = {"0", "1", "2", "3"};
            Path basePath = Paths.get(mediaId);

            for (String resolution : resolutions) {
                Files.createDirectories(basePath.resolve(resolution));
            }
            SegmentEventFactory segmentEventFactory = new SegmentEventFactory(
                    assetId,
                    mediaId,
                    mediaType,
                    cloudFrontURL,
                    4000,
                    s3Service,
                    transcoded_bucket_name,
                    bucket_name
            );
            HlsDirectoryWatcher watcher = new HlsDirectoryWatcher(
                    mediaId, segmentEventFactory,
    producer,
                    moderationJobProducer,
                    moderation
            );


            watcher.start();
           boolean transcoding =  transcoder.startTranscodingAsync(signedURL, mediaId, encryptionKey,mediaType, publicKeyURL);
        if(mediaType.equals(MediaType.STATIC) && transcoding){
            watcher.stop();
            watcher.getCompletionFuture().get();
                TranscodingFinishEventDTO finishEventDTO = new TranscodingFinishEventDTO();
                finishEventDTO.setMediaId(mediaId);
                finishEventDTO.setMediaType(MediaType.STATIC);
                producer.sendFinishEvent(finishEventDTO);
        }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }




    }
}
