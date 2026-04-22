package com.vsnt.services;

import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.vsnt.*;
import com.vsnt.common_lib.dtos.events.asset.transcoding.AssetTranscodingCompletedEvent;
import com.vsnt.common_lib.dtos.events.asset.transcoding.AssetTranscodingCompletedPayload;
import com.vsnt.dtos.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class JobProcessor {

    private static final Logger logger = LoggerFactory.getLogger(JobProcessor.class);

    private final VideoTranscoder transcoder;
    private final S3Service s3Service;
    private final SegmentEventProducer producer;
    private final RegistryService registryService;

    public JobProcessor(VideoTranscoder transcoder,
                        S3Service s3Service,
                        SegmentEventProducer producer,
                        RegistryService registryService) {
        this.transcoder = transcoder;
        this.s3Service = s3Service;
        this.producer = producer;
        this.registryService = registryService;
    }

    public void process(Channel channel, long deliveryTag, byte[] body, Map<String, Object> headers) {

        String message = new String(body, StandardCharsets.UTF_8);
        TranscodingJob job = new Gson().fromJson(message, TranscodingJob.class);

        String assetId = job.getAssetId();
        String mediaId = job.getJobId();

        int retryCount = extractRetry(headers);
        final int MAX_RETRIES = 3;

        logger.info("Processing job. mediaId={}, assetId={}, retryCount={}",
                mediaId, assetId, retryCount);

        Path basePath = Paths.get(mediaId, assetId);
        String outputPath = basePath.toString();

        HlsDirectoryWatcher watcher = null;

        try {
            String[] resolutions = {"0", "1", "2", "3"};

            for (String resolution : resolutions) {
                Files.createDirectories(basePath.resolve(resolution));
            }

            logger.info("Directories created for mediaId={}, path={}", mediaId, outputPath);

            String url = String.format("rtmp://rtmp_server:1935/live/%s", mediaId);
            logger.info("Starting stream consumption from URL={}", url);

            watcher = createWatcher(assetId, mediaId, outputPath);
            watcher.start();

            logger.info("HLS watcher started for mediaId={}", mediaId);

            registryService.notifyRegistry(mediaId, assetId);
            logger.info("Registry notified. mediaId={}, assetId={}", mediaId, assetId);

            TranscodeResult result = transcoder.startTranscodingAsync(
                    mediaId,
                    url,
                    outputPath,
                    job.getEncryptionKey(),
                    MediaType.LIVE,
                    System.getenv("PUBLIC_KEY_SERVER_URL") + "/" + mediaId + "/" + assetId
            );

            logger.info("Transcoding finished. status={}, mediaId={}", result.getStatus(), mediaId);

            handleResult(channel, deliveryTag, body, headers, result, retryCount, MAX_RETRIES, assetId, mediaId);

        } catch (Exception e) {
            logger.error("Job failed. mediaId={}, assetId={}", mediaId, assetId, e);
            handleFailure(channel, deliveryTag, body, headers, retryCount, MAX_RETRIES, assetId, mediaId, e);

        } finally {
            if (watcher != null) {
                logger.info("Stopping watcher for mediaId={}", mediaId);
                watcher.stop();
            }
        }
    }

    private int extractRetry(Map<String, Object> headers) {
        Object val = headers.get("x-retry-count");
        return (val instanceof Number) ? ((Number) val).intValue() : 0;
    }

    private HlsDirectoryWatcher createWatcher(String assetId, String mediaId, String outputPath) throws IOException {
        return new HlsDirectoryWatcher(
                outputPath,
                new SegmentEventFactory(
                        System.getenv("CDN_BASE_URL"),
                        4000,
                        s3Service,
                        System.getenv("AWS_TRANSCODED_BUCKET")
                ),
                producer,
                assetId,
                mediaId
        );
    }

    private void handleResult(Channel channel,
                              long deliveryTag,
                              byte[] body,
                              Map<String, Object> headers,
                              TranscodeResult result,
                              int retryCount,
                              int maxRetries,
                              String assetId,
                              String mediaId) throws Exception {

        switch (result.getStatus()) {

            case SUCCESS -> {
                logger.info("Transcoding SUCCESS. mediaId={}, assetId={}", mediaId, assetId);

                AssetTranscodingCompletedEvent event =
                        new AssetTranscodingCompletedEvent(
                                assetId,
                                Instant.now(),
                                AssetTranscodingCompletedPayload.builder().mediaId(mediaId).build()
                        );

                producer.sendFinishEvent(event);

                channel.basicAck(deliveryTag, false);
            }

            case STOPPED -> {
                logger.warn("Transcoding STOPPED. mediaId={}", mediaId);
                channel.basicReject(deliveryTag, false);
            }

            case FAILED, PARTIAL_FAILURE -> {
                logger.warn("Transcoding FAILED. Retrying... mediaId={}, retry={}", mediaId, retryCount);
                retry(channel, deliveryTag, body, headers, retryCount, maxRetries);
            }

            case INTERRUPTED -> {
                logger.warn("Transcoding INTERRUPTED. Requeueing... mediaId={}", mediaId);
                channel.basicNack(deliveryTag, false, true);
            }
        }
    }

    private void retry(Channel channel,
                       long deliveryTag,
                       byte[] body,
                       Map<String, Object> headers,
                       int retryCount,
                       int maxRetries) throws Exception {

        if (retryCount >= maxRetries) {
            logger.error("Max retries reached. Dropping message. retryCount={}", retryCount);
            channel.basicAck(deliveryTag, false);
            return;
        }

        Map<String, Object> newHeaders = new HashMap<>(headers);
        newHeaders.put("x-retry-count", retryCount + 1);

        AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
                .headers(newHeaders)
                .build();

        channel.basicPublish("", "your-queue", props, body);

        logger.info("Retrying job. newRetryCount={}", retryCount + 1);

        channel.basicAck(deliveryTag, false);
    }

    private void handleFailure(Channel channel,
                               long deliveryTag,
                               byte[] body,
                               Map<String, Object> headers,
                               int retryCount,
                               int maxRetries,
                               String assetId,
                               String mediaId,
                               Exception e) {

        logger.error("Handling failure. mediaId={}, retryCount={}", mediaId, retryCount, e);

        try {
            retry(channel, deliveryTag, body, headers, retryCount, maxRetries);
        } catch (Exception ex) {
            logger.error("Retry also failed. mediaId={}", mediaId, ex);
        }
    }
}