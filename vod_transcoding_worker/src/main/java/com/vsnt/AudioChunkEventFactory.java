package com.vsnt;


import com.vsnt.common_lib.dtos.jobs.transcription.TranscriptionJob;
import com.vsnt.dtos.ChunkInfo;
import com.vsnt.dtos.MediaType;
import com.vsnt.services.S3Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AudioChunkEventFactory {

    private static final Logger logger = LoggerFactory.getLogger(SegmentEventFactory.class);
    private final String cdnBaseUrl;
    private final long segmentDuration;
    private final S3Service s3Service;
    private final String transcodedBucketName;

    public AudioChunkEventFactory(String cdnBaseUrl,
                               long segmentDuration,
                               S3Service s3Service,
                               String transcodedBucketName) {
        this.cdnBaseUrl = cdnBaseUrl;
        this.segmentDuration = segmentDuration;
        this.s3Service = s3Service;
        this.transcodedBucketName = transcodedBucketName;
        logger.info("AudioChunkFactory initialized. bucket={}, cdn={}",
                transcodedBucketName, cdnBaseUrl);
    }
    public TranscriptionJob generate(Path segmentPath,
                                                String assetId,
                                                String mediaId,
                                                MediaType mediaType) {
        String fileName = segmentPath.getFileName().toString();
        try {
            ChunkInfo chunkInfo = extractChunkInfo(fileName);
            long sequenceNumber = chunkInfo.chunkNumber();
            double startTime = chunkInfo.start();
            double endTime = chunkInfo.end();


            String s3Key = "transcoded/" + assetId + "/audio"  + "/" + fileName;
            logger.debug("Uploading segment. mediaId={}, seq={}",
                    mediaId, sequenceNumber);
            s3Service.uploadSegment(transcodedBucketName, s3Key, segmentPath);

            logger.debug("Segment uploaded. mediaId={}, seq={}, ",
                    mediaId, sequenceNumber);
            String url = cdnBaseUrl + "/" + s3Key;
            return TranscriptionJob.builder()
                    .endTime(endTime)
                    .startTime(startTime)
                    .assetId(assetId)
                    .chunkUrl(url)
                    .mediaId(mediaId)
                    .chunkNumber(sequenceNumber)
                    .build();

        } catch (Exception e) {
            logger.error("Segment processing failed. mediaId={}, assetId={}, file={}",
                    mediaId, assetId, fileName, e);
            throw new RuntimeException("Segment processing failed", e);
        }
    }

    public static ChunkInfo extractChunkInfo(String filename) {
        // Strip extension
        String name = filename.contains(".")
                ? filename.substring(0, filename.lastIndexOf('.'))
                : filename;

        // Match pattern: {start}_{end}_{chunkNumber}
        Pattern pattern = Pattern.compile("^(\\d+)_(\\d+)_(\\d+)$");
        Matcher matcher = pattern.matcher(name);

        if (!matcher.matches()) {
            throw new IllegalArgumentException(
                    "Filename does not match expected pattern {start}_{end}_{chunkNumber}: " + filename
            );
        }

        int start       = Integer.parseInt(matcher.group(1));
        int end         = Integer.parseInt(matcher.group(2));
        int chunkNumber = Integer.parseInt(matcher.group(3));

        if (start < 0 || end < 0)        throw new IllegalArgumentException("Timestamps cannot be negative: "        + filename);
        if (start >= end)                 throw new IllegalArgumentException("Start must be less than end: "          + filename);
        if (chunkNumber < 0)              throw new IllegalArgumentException("Chunk number cannot be negative: "      + filename);

        return new ChunkInfo(start, end, chunkNumber);
    }

}