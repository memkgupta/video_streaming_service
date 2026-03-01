package com.vsnt.services;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.vsnt.config.S3Config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class S3Service {

    public String generatePresignedUrl(String bucketName, String key) throws Exception
    {
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, key);
        S3Config s3Config = new S3Config();
        AmazonS3 s3Client = s3Config.getS3Client();
        URL url = s3Client.generatePresignedUrl(request);
        return url.toString();
    }
    public Path fetchVideo(String key,String bucket,String downloadPath)
    {
        S3Config s3Config = new S3Config();
        AmazonS3 s3Client = s3Config.getS3Client();
        System.out.println(bucket+","+key);
        S3Object object = s3Client.getObject(bucket,key);
        S3ObjectInputStream s3is = object.getObjectContent();
        Path outputPath = Paths.get(downloadPath,key);
        try{
            Files.createDirectories(outputPath.getParent());
            try(OutputStream outputStream = Files.newOutputStream(outputPath)){
                s3is.transferTo(outputStream);
            }
            catch(IOException e){
                e.printStackTrace();
                throw e;
            }
            finally {
                s3is.close();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException("Unable to fetch video");
        }

        return outputPath;
    }
    public void uploadSegment(String bucket, String key, Path path) throws IOException {

        if (!Files.exists(path)) {
            throw new IOException("File does not exist: " + path);
        }

        S3Config s3Config = new S3Config();
        AmazonS3 client = s3Config.getS3Client();

        File file = path.toFile();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.length());

        // Set proper content type
        if (key.endsWith(".ts")) {
            metadata.setContentType("video/MP2T");
        } else if (key.endsWith(".m3u8")) {
            metadata.setContentType("application/vnd.apple.mpegurl");
        }

        try (InputStream inputStream = Files.newInputStream(path)) {

            PutObjectRequest putRequest =
                    new PutObjectRequest(bucket, key, inputStream, metadata);

            client.putObject(putRequest);

            System.out.println("Uploaded to S3: s3://" + bucket + "/" + key);

        } catch (AmazonServiceException e) {
            System.err.println("S3 rejected the request: " + e.getErrorMessage());
            throw e;
        } catch (SdkClientException e) {
            System.err.println("Client error while uploading: " + e.getMessage());
            throw e;
        }
    }
}
