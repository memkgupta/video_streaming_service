package com.vsnt;


public class App 
{
    public static void main(String[] args) throws Exception {

        FFmpegRunner ffmpegRunner = new FFmpegRunner();

        ffmpegRunner.start();

Thread t = new Thread(()->{

    S3Uploader uploader = new S3Uploader();
    KafkaEventProducer kafkaProducer =
            new KafkaEventProducer();
    SegmentWatcher watcher =
            new SegmentWatcher(uploader, kafkaProducer);

    try {
        watcher.start();
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
});
t.start();
    }
}
