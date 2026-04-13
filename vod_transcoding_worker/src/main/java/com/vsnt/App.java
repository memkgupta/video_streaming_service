package com.vsnt;


import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.vsnt.config.RabbitMQConfig;
import com.vsnt.config.Secrets;
import com.vsnt.dtos.MediaType;
import com.vsnt.dtos.TranscodingFailedDTO;
import com.vsnt.dtos.TranscodingFinishEventDTO;
import com.vsnt.dtos.TranscodingJob;
import com.vsnt.services.HlsDirectoryWatcher;
import com.vsnt.services.S3Service;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

public class App
{
    public static void main( String[] args )
    {
        S3Service s3Service = new S3Service();

        String bucket_name = System.getenv("AWS_RAW_BUCKET");
        String transcoded_bucket_name = System.getenv("AWS_TRANSCODED_BUCKET");
        String kafka_brokers = System.getenv("KAFKA_BOOTSTRAP_SERVERS");
        String kafka_topic_segment_update = System.getenv("TRANSCODING_UPDATE_TOPIC");
        String kafka_topic_finish = System.getenv("TRANSCODING_FINISH_TOPIC");
        String kafka_topic_fail = System.getenv("TRANSCODING_FAIL_TOPIC");
        String publicKeyServerURL = System.getenv("PUBLIC_KEY_SERVER_URL");
        String cloudFrontURL = System.getenv("CDN_BASE_URL");
        if( bucket_name == null){
            System.out.println("Missing environment variables");
            System.exit(1);
        }
        RabbitMQConfig config  = new RabbitMQConfig();
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        VideoTranscoder videoTranscoder = new VideoTranscoder(executorService);
        SegmentEventProducer producer = new SegmentEventProducer(kafka_brokers,kafka_topic_segment_update,kafka_topic_finish,kafka_topic_fail);
        try {
            Channel channel = config.getChannel();
            DeliverCallback callback = (consumerTag, delivery) -> {
                // ===== Extract headers safely =====
                Map<String, Object> headers = delivery.getProperties().getHeaders();
                if (headers == null) {
                    headers = new java.util.HashMap<>();
                }
                // ===== Get retry count (default = 0) =====
                int retryCount = 0;
                if (headers.containsKey("x-retry-count")) {
                    retryCount = (int) headers.get("x-retry-count");
                }
                final int MAX_RETRIES = 3; // retry limit
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                long deliveryTag = delivery.getEnvelope().getDeliveryTag();
                TranscodingJob job = new Gson().fromJson(message, TranscodingJob.class);
                String assetId = job.getAssetId();
                String mediaId = job.getJobId();
                try {

                    String url = s3Service.generatePresignedUrl(bucket_name, job.getKey());
                    String[] resolutions = {"0", "1", "2", "3"};
                    Path basePath = Paths.get(mediaId, assetId);
                    for (String resolution : resolutions) {
                        Files.createDirectories(basePath.resolve(resolution));
                    }

                    String outputPath = basePath.toString();

                    SegmentEventFactory segmentEventFactory = new SegmentEventFactory(
                            cloudFrontURL,
                            4000,
                            s3Service,
                            transcoded_bucket_name
                    );

                    HlsDirectoryWatcher watcher = new HlsDirectoryWatcher(
                            outputPath,
                            segmentEventFactory,
                            producer,
                            assetId,
                            mediaId
                    );
                    watcher.start();
                    boolean transcoded = videoTranscoder.startTranscodingAsync(
                            url,
                            outputPath,
                            job.getEncryptionKey(),
                            MediaType.STATIC,
                            publicKeyServerURL+"/"+mediaId+"/"+assetId
                    );

                    if (transcoded) {
                        TranscodingFinishEventDTO dto = new TranscodingFinishEventDTO();
                        dto.setMediaType(MediaType.STATIC);
                        dto.setMediaId(mediaId);

                        producer.sendFinishEvent(dto);
                        channel.basicAck(deliveryTag, false);
                        System.out.println("Done: " + message);
                    }
                    else {
                        throw new RuntimeException("Transcoding failed");
                    }
                    watcher.stop();
                    //  ACK only after success
                } catch (Exception e) {
                    System.out.println("Failed: " + message + " | Retry: " + retryCount);
                    try {
                        if (retryCount >= MAX_RETRIES) {
                            System.out.println("Max retries reached.");
                            // Remove from queue
                            TranscodingFailedDTO failedDTO = new TranscodingFailedDTO();
                            failedDTO.setMediaType(MediaType.STATIC);
                            failedDTO.setMessage(e.getMessage());
                            failedDTO.setAssetId(assetId);
                            failedDTO.setMediaId(mediaId);
                            producer.sendFailedEvent(failedDTO);
                            channel.basicAck(deliveryTag, false);
                        } else {
                            // Increment retry count
                            headers.put("x-retry-count", retryCount + 1);
                            AMQP.BasicProperties newProps = new AMQP.BasicProperties.Builder()
                                    .headers(headers)
                                    .build();
                            // Re-publish message with updated retry count
                            channel.basicPublish(
                                    delivery.getEnvelope().getExchange(),
                                    delivery.getEnvelope().getRoutingKey(),
                                    newProps,
                                    delivery.getBody()
                            );
                            // ACK original message (important to avoid duplicates)
                            channel.basicAck(deliveryTag, false);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            };
            channel.basicConsume(Secrets.RABBITMQ_QUEUE, false, callback, consumerTag -> {});
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }

    }
}
