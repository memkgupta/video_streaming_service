package com.vsnt;

import com.vsnt.config.RabbitMQConfig;
import com.vsnt.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class App {

    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {

        logger.info("Starting VSNT Transcoding Worker...");

        try {
            S3Service s3Service = new S3Service();

            String bucketName = System.getenv("AWS_RAW_BUCKET");

            String kafkaBrokers = System.getenv("KAFKA_BOOTSTRAP_SERVERS");
            String kafkaTopicSegmentUpdate = System.getenv("TRANSCODING_UPDATE_TOPIC");
            String kafkaTopicFinish = System.getenv("TRANSCODING_FINISH_TOPIC");
            String kafkaTopicFail = System.getenv("TRANSCODING_FAIL_TOPIC");
            String registryServerURL = System.getenv("REGISTRY_SERVER_URL");

            if (bucketName == null || kafkaBrokers == null || registryServerURL == null) {
                logger.error("Missing required environment variables. bucket={}, kafka={}, registry={}",
                        bucketName, kafkaBrokers, registryServerURL);
                System.exit(1);
            }

            logger.info("Environment loaded successfully");

            RabbitMQConfig config = new RabbitMQConfig();

            ExecutorService executorService =
                    Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

            VideoTranscoder videoTranscoder = new VideoTranscoder(executorService);

            SegmentEventProducer producer = new SegmentEventProducer(
                    kafkaBrokers,
                    kafkaTopicSegmentUpdate,
                    kafkaTopicFinish,
                    kafkaTopicFail
            );

            HttpClient httpClient = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();

            RegistryService registryService = new RegistryService(httpClient, registryServerURL);

            logger.info("Registering worker with registry...");
            registryService.getId();

            logger.info("Worker registered. containerId={}", registryService.getContainerId());

            JobProcessor jobProcessor = new JobProcessor(
                    videoTranscoder,
                    s3Service,
                    producer,
                    registryService
            );

            StreamManager streamManager = new DefaultStreamManager(videoTranscoder);

            HeartbeatService heartbeatService = new HeartbeatService(
                    httpClient,
                    registryServerURL,
                    registryService.getContainerId(),
                    streamManager
            );

            heartbeatService.start(5);
            logger.info("Heartbeat service started");

            Consumer consumer = new Consumer(
                    config,
                    jobProcessor,
                    Executors.newFixedThreadPool(4),
                    streamManager
            );

            logger.info("Starting RabbitMQ consumer...");
            consumer.start();

        } catch (Exception e) {
            logger.error("Fatal error during application startup", e);
            System.exit(1);
        }
    }
}