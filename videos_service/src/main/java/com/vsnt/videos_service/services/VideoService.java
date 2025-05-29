package com.vsnt.videos_service.services;

import com.vsnt.videos_service.dtos.VideoDTO;
import com.vsnt.videos_service.entities.Video;
import com.vsnt.videos_service.entities.VideoUploadStatusEnum;
import com.vsnt.videos_service.entities.VideoVisibilityStatusEnum;
import com.vsnt.videos_service.exceptions.InternalServerError;
import com.vsnt.videos_service.exceptions.VideoNotFoundException;
import com.vsnt.videos_service.repositories.VideoRepository;
import com.vsnt.videos_service.specifications.SpecificationBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class VideoService {
    private final VideoRepository videoRepository;
    private final SpecificationBuilder specificationBuilder;
    public Video createVideoDraft(String userId,String channelId)
    {

            Video video = new Video();
            video.setChannelId(channelId);
            video.setVisibilityStatus(VideoVisibilityStatusEnum.DRAFT);
            video.setUserId(userId);
            video.setUploadedAt(new Timestamp(System.currentTimeMillis()));
            return videoRepository.save(video);


    }
    public Video fillDetails(VideoDTO videoDTO, String userId,String videoId) {

            Video video = videoRepository.findById(videoId).orElse(null);
            if(video == null || !video.getUserId().equals(userId)){
                throw new VideoNotFoundException(videoId);
            }
            System.out.println(videoDTO);
            video.setTitle(videoDTO.getTitle());
            video.setDescription(videoDTO.getDescription());
            video.setStatus(VideoUploadStatusEnum.UPLOADING);

            video.setAssetId(videoDTO.getAssetId());

            return videoRepository.save(video);

    }
    public Video getVideo(String videoId){

            Video video = videoRepository.findById(videoId).orElse(null);

            return video;

    }
    public Page<Video> getAllVideos(Map<String,String> params, Pageable pageable){

            Specification<Video> specification = specificationBuilder.build(params);


            Page<Video> videos = videoRepository.findAll(specification,pageable);

            return videos;

    }
    public Video updateVideo(String videoId, VideoDTO videoDTO, String userId) {

            Video video = videoRepository.findById(videoId).orElse(null);
            if(video == null || !video.getUserId().equals(userId))
            {
                throw new VideoNotFoundException(videoId);
            }
            if(videoDTO.getTitle() != null && !videoDTO.getTitle().isEmpty() &&!videoDTO.getTitle().equals(video.getTitle()))
            {
                video.setTitle(videoDTO.getTitle());
            }
            if(videoDTO.getDescription() != null && !videoDTO.getDescription().isEmpty() &&!videoDTO.getDescription().equals(video.getDescription()))
            {
                video.setDescription(videoDTO.getDescription());
            }
            if(videoDTO.getThumbnailUrl()!=null && !videoDTO.getThumbnailUrl().isEmpty() &&!videoDTO.getThumbnailUrl().equals(video.getThumbnailUrl()) )
            {
                video.setThumbnailUrl(videoDTO.getThumbnailUrl());
            }
            return videoRepository.save(video);

    }
    public void deleteVideo(String videoId,String userId)
    {

            Video video = videoRepository.findById(videoId).orElse(null);
            if(video == null ||!video.getUserId().equals(userId))
            {
                throw new VideoNotFoundException(videoId);
            }
            videoRepository.delete(video);



    }
    public void updateVideoUploadStatus(String videoId,VideoUploadStatusEnum status)
    {

            Video video = videoRepository.findById(videoId).orElse(null);
            if(video!=null){
                video.setStatus(status);
                if(status.equals(VideoUploadStatusEnum.COMPLETED))
                {
                    video.setVisibilityStatus(VideoVisibilityStatusEnum.PUBLISHED);
                }
                videoRepository.save(video);
            }



    }
    public Video publishVideo(String videoId,String userId)
    {

            Video video = videoRepository.findById(videoId).orElse(null);
            if(video==null )
            {
                throw new VideoNotFoundException(videoId);
            }

            video.setVisibilityStatus(VideoVisibilityStatusEnum.SCHEDULED);
           return videoRepository.save(video);


    }
}
