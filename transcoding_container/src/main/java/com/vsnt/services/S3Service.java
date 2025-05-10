package com.vsnt.services;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.vsnt.config.S3Config;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class S3Service {
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
    public void uploadObject(String bucket,String keyPrefix,Path directory) throws IOException {
        S3Config s3Config = new S3Config();
        AmazonS3 client = s3Config.getS3Client();

        Files.walk(directory)
                .filter(Files::isRegularFile)
                .forEach(path -> {
                    String key = keyPrefix+"/"+directory.relativize(path).toString().replace("\\","/");
                    try{
                        client.putObject(new PutObjectRequest(bucket,key,path.toFile()));
                    }
                    catch(Exception e){
                        e.printStackTrace();
                        throw new RuntimeException("Unable to upload file " + key + " → " + e.getMessage());
                    }
                });
    }
}
