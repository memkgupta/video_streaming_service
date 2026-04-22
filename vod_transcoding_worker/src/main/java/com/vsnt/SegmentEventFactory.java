package com.vsnt;

import com.vsnt.dtos.MediaType;
import com.vsnt.dtos.ResolutionEnum;
import com.vsnt.dtos.TranscodingSegmentUpdateDTO;
import com.vsnt.services.S3Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Map;

public class SegmentEventFactory {

    private static final Logger logger = LoggerFactory.getLogger(SegmentEventFactory.class);

    private final String cdnBaseUrl;
    private final long segmentDuration;
    private final S3Service s3Service;
    private final String transcodedBucketName;

    private static final Map<String, String> VARIANT_MAP = Map.of(
            "0", "360p",
            "1", "480p",
            "2", "720p",
            "3", "1080p"
    );

    public SegmentEventFactory(String cdnBaseUrl,
                               long segmentDuration,
                               S3Service s3Service,
                               String transcodedBucketName) {

        this.cdnBaseUrl = cdnBaseUrl;
        this.segmentDuration = segmentDuration;
        this.s3Service = s3Service;
        this.transcodedBucketName = transcodedBucketName;

        logger.info("SegmentEventFactory initialized. bucket={}, cdn={}",
                transcodedBucketName, cdnBaseUrl);
    }

    public TranscodingSegmentUpdateDTO generate(Path segmentPath,
                                                String assetId,
                                                String mediaId,
                                                MediaType mediaType) {

        String fileName = segmentPath.getFileName().toString();

        try {
            long sequenceNumber = extractSequenceNumber(fileName);

            String variant = segmentPath.getParent().getFileName().toString();
            String resolutionFolder = VARIANT_MAP.get(variant);

            if (resolutionFolder == null) {
                logger.error("Unknown variant. variant={}, path={}, mediaId={}",
                        variant, segmentPath, mediaId);
                throw new IllegalArgumentException("Unknown variant: " + variant);
            }

            ResolutionEnum resolution =
                    ResolutionEnum.valueOf("RESOLUTION_" + resolutionFolder.replace("p", "P"));

            String s3Key = "transcoded/" + assetId + "/" + resolutionFolder + "/" + fileName;

            logger.debug("Uploading segment. mediaId={}, seq={}, res={}",
                    mediaId, sequenceNumber, resolutionFolder);

            long start = System.currentTimeMillis();

            s3Service.uploadSegment(transcodedBucketName, s3Key, segmentPath);

            long duration = System.currentTimeMillis() - start;

            logger.debug("Segment uploaded. mediaId={}, seq={}, time={}ms",
                    mediaId, sequenceNumber, duration);

            String url = cdnBaseUrl + "/" + s3Key;

            return new TranscodingSegmentUpdateDTO(
                    assetId,
                    url,
                    sequenceNumber,
                    mediaId,
                    segmentDuration,
                    resolution,
                    mediaType
            );

        } catch (Exception e) {
            logger.error("Segment processing failed. mediaId={}, assetId={}, file={}",
                    mediaId, assetId, fileName, e);


            throw new RuntimeException("Segment processing failed", e);
        }
    }

    private long extractSequenceNumber(String fileName) {
        try {
            String number = fileName.replaceAll("[^0-9]", "");
            return Long.parseLong(number);
        } catch (Exception e) {
            logger.error("Failed to extract sequence number. file={}", fileName, e);
            throw e;
        }
    }
}