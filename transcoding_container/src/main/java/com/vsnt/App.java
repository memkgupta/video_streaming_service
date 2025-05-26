package com.vsnt;

import com.vsnt.dtos.UpdateRequestDTO;
import com.vsnt.services.APIService;
import com.vsnt.services.S3Service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        S3Service s3Service = new S3Service();
        String file_key = System.getenv("FILE_KEY");
        String videoId = System.getenv("VIDEO_ID");
        String bucket_name = System.getenv("BUCKET_NAME");
        String transcoded_bucket_name = System.getenv("TRANSCODED_BUCKET_NAME");
        String transcoderAPIURL = System.getenv("UPDATE_API_URL");
        String cloudFrontURL = System.getenv("CLOUDFRONT_URL");
        if(file_key == null || bucket_name == null){
            System.out.println("Missing environment variables");
            System.exit(1);
        }

        Path filePath = s3Service.fetchVideo(file_key,bucket_name,"original/");
        VideoTranscoder transcoder = new VideoTranscoder();
        transcoder.transcodeVideo(filePath.toAbsolutePath().toString(), "transcoded");
        try{
            file_key = file_key.replace("uploads/","transcoded/");
            s3Service.uploadObject(transcoded_bucket_name,file_key, Paths.get("/app/transcoded"));
            // send api request to the transcoder for updating status
            UpdateRequestDTO dto = new UpdateRequestDTO();
            dto.setVideoId(videoId);
            dto.setTimestamp(new Timestamp(System.currentTimeMillis()));
            dto.setStatus("SUCCESS");
            dto.setUrl(cloudFrontURL+"/"+file_key+"/index.m3u8");
System.out.println("VideoId ************************* "+dto.getVideoId()+"******"+videoId);
            APIService service = new APIService(transcoderAPIURL);
            service.sendUpdateRequest(dto);
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }
}
