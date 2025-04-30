package com.vsnt;

import com.vsnt.config.FFMPEGConfig;
import com.vsnt.services.S3Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class VideoTranscoder {

   public boolean transcodeVideo(String inputPath,String outputPath)  {

if(!Files.exists(Paths.get(inputPath))) {
   System.out.println(inputPath + " does not exist");
   System.out.println("Input path does not exist");
   return false;
}
else{
   System.out.println(inputPath + " exists");
}
      String[] resolutions = {"360p", "480p", "720p", "1080p"};
      try{
         Path path = Paths.get(outputPath);
         System.out.println(inputPath+" ye rha bhai");
         if(!Files.exists(path))
         {
            for (String resolution : resolutions) {
               Path resolutionPath = path.resolve(resolution);
               Files.createDirectories(resolutionPath);
               System.out.println("Created directory: " + resolutionPath);
            }
         }
      }
      catch (IOException e){
         e.printStackTrace();
      }
      FFMPEGConfig config = new FFMPEGConfig(inputPath, outputPath);


      for(String command : config.getFfmpegCommands())
      {
         System.out.println(command);
         try{
            ProcessBuilder pb = new ProcessBuilder();
            pb.redirectErrorStream(true);
             pb.command("sh", "-c", command);
            Process process = pb.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
               String line;
               while ((line = reader.readLine()) != null) {
                  // Print the logs to the console
                  System.out.println(line);
               }
            }
            int exitCode = process.waitFor();
            if (exitCode == 0) {
               System.out.println("FFmpeg command executed successfully!");

            } else {
               System.err.println("Error occurred during FFmpeg execution. Exit code: " + exitCode);
               return false;
            }
         }
         catch(Exception e){
            e.printStackTrace();
            System.out.println("Transcoding failed");
            return false;
         }

      }
      Path masterPlaylistPath = Paths.get(outputPath, "index.m3u8");
      try{
         Files.writeString(masterPlaylistPath,config.getMasterPlaylistContent());
      }
      catch(IOException e){

         e.printStackTrace();
         return false;
      }

      return true;
   }
}
