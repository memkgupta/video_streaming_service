package com.vsnt;
import com.sun.source.doctree.SeeTree;
import com.vsnt.config.RabbitMQConfig;
import com.vsnt.services.*;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * Hello world!
 *
 */
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
        String registryServerURL = System.getenv("REGISTRY_SERVER_URL");
        if( bucket_name == null){
            System.out.println("Missing environment variables");
            System.exit(1);
        }
        RabbitMQConfig config  = new RabbitMQConfig();
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        VideoTranscoder videoTranscoder = new VideoTranscoder(executorService);
        SegmentEventProducer producer = new SegmentEventProducer(kafka_brokers,kafka_topic_segment_update,kafka_topic_finish,kafka_topic_fail);
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        RegistryService registryService = new RegistryService( httpClient, registryServerURL);
        registryService.getId();
        JobProcessor jobProcessor = new JobProcessor(
                videoTranscoder,
                s3Service,
                producer,
                registryService
        );

        StreamManager streamManager = new DefaultStreamManager(videoTranscoder);
        HeartbeatService heartbeatService = new HeartbeatService(httpClient,registryServerURL,registryService.getContainerId(),streamManager);
        heartbeatService.start(5);
        Consumer consumer = new Consumer(config,jobProcessor,Executors.newFixedThreadPool(4),streamManager);
       try{
           consumer.start();
       }
       catch(Exception e){
           e.printStackTrace();
       }
    }
}
