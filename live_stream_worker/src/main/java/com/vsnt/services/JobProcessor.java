package com.vsnt.services;

import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.vsnt.*;
import com.vsnt.common_lib.dtos.events.asset.transcoding.AssetTranscodingCompletedEvent;
import com.vsnt.common_lib.dtos.events.asset.transcoding.AssetTranscodingCompletedPayload;
import com.vsnt.dtos.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class JobProcessor {

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
        System.out.println("Asset ID: " + assetId+" "+mediaId);
        int retryCount = extractRetry(headers);
        final int MAX_RETRIES = 3;

        Path basePath = Paths.get(mediaId, assetId);
        String outputPath = basePath.toString();

        HlsDirectoryWatcher watcher = null;

        try {
            String[] resolutions = {"0", "1", "2", "3"};


            for (String resolution : resolutions) {
                System.out.println("Creating directory");
                Files.createDirectories(basePath.resolve(resolution));
            }

            String url = String.format("rtmp://rtmp_server:1935/live/%s",mediaId);
            System.out.println("URL: " + url);
            watcher = createWatcher(assetId, mediaId, outputPath);

            watcher.start();
            System.out.println("Watcher up and running");
            registryService.notifyRegistry(mediaId, assetId);
            System.out.println("Registry Notified");
            TranscodeResult result = transcoder.startTranscodingAsync(
                    mediaId,
                    url,
                    outputPath,
                    job.getEncryptionKey(),
                    MediaType.LIVE,
                    System.getenv("PUBLIC_KEY_SERVER_URL") + "/" + mediaId + "/" + assetId
            );
            handleResult(channel, deliveryTag, body, headers, result, retryCount, MAX_RETRIES, assetId, mediaId);

        } catch (Exception e) {
            handleFailure(channel, deliveryTag, body, headers, retryCount, MAX_RETRIES, assetId, mediaId, e);
        } finally {
            if (watcher != null) watcher.stop();
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
                AssetTranscodingCompletedEvent assetTranscodingCompletedEvent=new AssetTranscodingCompletedEvent(
                        assetId, Instant.now(), AssetTranscodingCompletedPayload.builder().mediaId(mediaId).build()
                );
                producer.sendFinishEvent(assetTranscodingCompletedEvent);
                channel.basicAck(deliveryTag, false);
                System.out.println("Done: " + result.getMessage());

            }

            case STOPPED -> {

                channel.basicReject(deliveryTag, false);
            }

            case FAILED, PARTIAL_FAILURE -> {
                retry(channel, deliveryTag, body, headers, retryCount, maxRetries);
            }

            case INTERRUPTED -> {
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
            channel.basicAck(deliveryTag, false);
            return;
        }

        Map<String, Object> newHeaders = new HashMap<>(headers);
        newHeaders.put("x-retry-count", retryCount + 1);

        AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
                .headers(newHeaders)
                .build();

        channel.basicPublish("", "your-queue", props, body);

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

        try {
            retry(channel, deliveryTag, body, headers, retryCount, maxRetries);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}