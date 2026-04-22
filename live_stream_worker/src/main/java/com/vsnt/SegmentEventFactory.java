package com.vsnt;

import com.vsnt.dtos.MediaType;
import com.vsnt.dtos.ResolutionEnum;
import com.vsnt.dtos.TranscodingSegmentUpdateDTO;
import com.vsnt.services.S3Service;

import java.nio.file.Path;
import java.util.Map;

public class SegmentEventFactory {

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

    public SegmentEventFactory(
                               String cdnBaseUrl,
                               long segmentDuration,
                               S3Service s3Service,
                               String transcodedBucketName) {


        this.cdnBaseUrl = cdnBaseUrl;
        this.segmentDuration = segmentDuration;
        this.s3Service = s3Service;
        this.transcodedBucketName = transcodedBucketName;

    }

    public TranscodingSegmentUpdateDTO generate(Path segmentPath ,String assetId , String mediaId , MediaType mediaType) {

        String fileName = segmentPath.getFileName().toString();

        long sequenceNumber = extractSequenceNumber(fileName);

        //  variant folder (0/1/2/3)
        String variant = segmentPath.getParent().getFileName().toString();

        //  MAP to actual resolution
        String resolutionFolder = VARIANT_MAP.get(variant);

        if (resolutionFolder == null) {
            throw new IllegalArgumentException("Unknown variant: " + variant);
        }

        ResolutionEnum resolution =
                ResolutionEnum.valueOf("RESOLUTION_" + resolutionFolder.replace("p", "P"));

        String s3Key = "transcoded/" + assetId + "/" + resolutionFolder + "/" + fileName;

        try {
            s3Service.uploadSegment(
                    transcodedBucketName,
                    s3Key,
                    segmentPath
            );

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
            e.printStackTrace();
        }

        return null;
    }

    private long extractSequenceNumber(String fileName) {

        String number = fileName
                .replaceAll("[^0-9]", ""); // works for mp4, timestamps, etc.
        return Long.parseLong(number);

    }
}