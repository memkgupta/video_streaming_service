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

    private static final int MAX_RETRIES = 3;

    public String generatePresignedUrl(String bucketName, String key) throws Exception {
        logger.info("Generating presigned URL. bucket={}, key={}", bucketName, key);

        AmazonS3 s3Client = new S3Config().getS3Client();

        URL url = s3Client.generatePresignedUrl(
                new GeneratePresignedUrlRequest(bucketName, key)
        );

        return url.toString();
    }

    public Path fetchVideo(String key, String bucket, String downloadPath) {

        logger.info("Fetching video from S3. bucket={}, key={}", bucket, key);

        AmazonS3 s3Client = new S3Config().getS3Client();
        Path outputPath = Paths.get(downloadPath, key);

        try (S3Object object = s3Client.getObject(bucket, key);
             S3ObjectInputStream s3is = object.getObjectContent()) {

            Files.createDirectories(outputPath.getParent());

            long start = System.currentTimeMillis();

            try (OutputStream outputStream = Files.newOutputStream(outputPath)) {
                s3is.transferTo(outputStream);
            }

            long duration = System.currentTimeMillis() - start;

            logger.info("Video fetched successfully. path={}, time={}ms",
                    outputPath, duration);

            return outputPath;

        } catch (Exception e) {
            logger.error("Failed to fetch video. bucket={}, key={}", bucket, key, e);
            throw new RuntimeException("Unable to fetch video", e);
        }
    }

    public void uploadSegment(String bucket, String key, Path path) throws IOException {

        if (!Files.exists(path)) {
            logger.error("File does not exist. path={}", path);
            throw new IOException("File does not exist: " + path);
        }

        AmazonS3 client = new S3Config().getS3Client();
        File file = path.toFile();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.length());

        if (key.endsWith(".ts")) {
            metadata.setContentType("video/MP2T");
        } else if (key.endsWith(".m3u8")) {
            metadata.setContentType("application/vnd.apple.mpegurl");
        }

        int attempt = 0;
        long delay = 1000;

        while (attempt < MAX_RETRIES) {
            try {
                attempt++;

                logger.debug("Uploading segment. attempt={}, bucket={}, key={}, size={}",
                        attempt, bucket, key, file.length());

                long start = System.currentTimeMillis();

                PutObjectRequest request = new PutObjectRequest(bucket, key, file)
                        .withMetadata(metadata);

                client.putObject(request);

                long duration = System.currentTimeMillis() - start;

                logger.info("Upload success. s3://{}/{} ({} ms)", bucket, key, duration);

                return;

            } catch (AmazonServiceException e) {
                logger.error("S3 rejected upload. attempt={}, bucket={}, key={}",
                        attempt, bucket, key, e);

                if (attempt >= MAX_RETRIES) throw e;

            } catch (SdkClientException e) {
                logger.warn("Transient S3 client error. attempt={}, bucket={}, key={}",
                        attempt, bucket, key, e);

                if (attempt >= MAX_RETRIES) throw e;

            }

            try {
                Thread.sleep(delay);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Upload retry interrupted", ie);
            }

            delay = Math.min(delay * 2, 8000); // exponential backoff
        }
    }
}