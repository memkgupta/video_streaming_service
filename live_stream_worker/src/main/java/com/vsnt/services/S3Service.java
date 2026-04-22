package com.vsnt.services;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.vsnt.config.S3Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.*;

public class S3Service {

    private static final Logger logger = LoggerFactory.getLogger(S3Service.class);

    public String generatePresignedUrl(String bucketName, String key) throws Exception {
        logger.info("Generating presigned URL. bucket={}, key={}", bucketName, key);

        S3Config s3Config = new S3Config();
        AmazonS3 s3Client = s3Config.getS3Client();

        URL url = s3Client.generatePresignedUrl(
                new GeneratePresignedUrlRequest(bucketName, key)
        );

        logger.debug("Presigned URL generated successfully for key={}", key);

        return url.toString();
    }

    public Path fetchVideo(String key, String bucket, String downloadPath) {

        logger.info("Fetching video from S3. bucket={}, key={}", bucket, key);

        S3Config s3Config = new S3Config();
        AmazonS3 s3Client = s3Config.getS3Client();

        Path outputPath = Paths.get(downloadPath, key);

        try (S3Object object = s3Client.getObject(bucket, key);
             S3ObjectInputStream s3is = object.getObjectContent()) {

            Files.createDirectories(outputPath.getParent());

            try (OutputStream outputStream = Files.newOutputStream(outputPath)) {
                long start = System.currentTimeMillis();

                s3is.transferTo(outputStream);

                long duration = System.currentTimeMillis() - start;

                logger.info("Video fetched successfully. path={}, time={}ms",
                        outputPath, duration);
            }

        } catch (AmazonServiceException e) {
            logger.error("S3 service error while fetching video. bucket={}, key={}", bucket, key, e);
            throw e;

        } catch (SdkClientException e) {
            logger.error("S3 client error while fetching video. bucket={}, key={}", bucket, key, e);
            throw e;

        } catch (Exception e) {
            logger.error("Unexpected error while fetching video. bucket={}, key={}", bucket, key, e);
            throw new RuntimeException("Unable to fetch video", e);
        }

        return outputPath;
    }

    public void uploadSegment(String bucket, String key, Path path) throws IOException {

        if (!Files.exists(path)) {
            logger.error("Upload failed. File does not exist: {}", path);
            throw new IOException("File does not exist: " + path);
        }

        logger.debug("Uploading segment. bucket={}, key={}, path={}", bucket, key, path);

        S3Config s3Config = new S3Config();
        AmazonS3 client = s3Config.getS3Client();

        File file = path.toFile();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.length());

        if (key.endsWith(".ts")) {
            metadata.setContentType("video/MP2T");
        } else if (key.endsWith(".m3u8")) {
            metadata.setContentType("application/vnd.apple.mpegurl");
        }

        try {
            long start = System.currentTimeMillis();

            PutObjectRequest request = new PutObjectRequest(bucket, key, file)
                    .withMetadata(metadata);

            client.putObject(request);

            long duration = System.currentTimeMillis() - start;

            logger.info("Segment uploaded successfully. s3://{}/{} ({} ms)",
                    bucket, key, duration);

        } catch (AmazonServiceException e) {
            logger.error("S3 rejected upload. bucket={}, key={}", bucket, key, e);
            throw e;

        } catch (SdkClientException e) {
            logger.error("Client error during upload. bucket={}, key={}", bucket, key, e);
            throw e;
        }
    }
}