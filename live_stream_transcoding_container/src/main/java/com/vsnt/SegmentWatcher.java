package com.vsnt;



import java.io.IOException;
import java.nio.file.*;

public class SegmentWatcher {

    private final S3Uploader uploader;
    private final KafkaEventProducer kafkaProducer;

    public SegmentWatcher(
            S3Uploader uploader,
            KafkaEventProducer kafkaProducer) {

        this.uploader = uploader;
        this.kafkaProducer = kafkaProducer;
    }

    public void start() throws Exception {

        WatchService watchService =
                FileSystems.getDefault().newWatchService();

        Path path = Paths.get(AppConfig.OUTPUT_DIR);

        path.register(
                watchService,
                StandardWatchEventKinds.ENTRY_CREATE
        );

        System.out.println("Watching for segments...");

        while (true) {

            WatchKey key = watchService.take();

            for (WatchEvent<?> event : key.pollEvents()) {

                Path fileName = (Path) event.context();
                System.out.println("**********************"+fileName+"**********");
                if (!fileName.toString().endsWith(".ts"))
                    continue;

                Path fullPath = path.resolve(fileName);

                String s3Key = uploader.upload(fullPath);

                String url =
                        AppConfig.CDN_BASE_URL + s3Key;

                long segmentId =
                        extractSegmentNumber(fileName.toString());

                long duration = 4; // seconds





                TranscodingSegmentUpdateDTO dto =
                        new TranscodingSegmentUpdateDTO();
                dto.setMediaId(AppConfig.MEDIA_ID);
                dto.setSequenceNumber(segmentId);
                dto.setUrl(url);
                dto.setAssetId(AppConfig.ASSET_ID);
                dto.setDuration(duration);
                kafkaProducer.send(dto);
                try {
                    Files.delete(fullPath);
                    System.out.println("File deleted successfully");
                } catch (IOException e) {
                    System.out.println("Failed to delete file: " + e.getMessage());
                }
            }

            key.reset();
        }
    }
    private long extractSegmentNumber(String fileName) {

        return Long.parseLong(
                fileName
                        .replace("segment_", "")
                        .replace(".ts", "")
        );
    }
}
