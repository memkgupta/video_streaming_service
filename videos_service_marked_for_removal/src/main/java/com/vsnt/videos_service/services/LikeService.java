package com.vsnt.videos_service.services;

import com.vsnt.videos_service.entities.Like;
import com.vsnt.videos_service.entities.Video;
import com.vsnt.videos_service.exceptions.BadRequestException;
import com.vsnt.videos_service.exceptions.InternalServerError;
import com.vsnt.videos_service.exceptions.VideoNotFoundException;
import com.vsnt.videos_service.repositories.LikeRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final VideoService videoService;
    private final LikeRepository likeRepository;

    public void postLike(String userId, String videoId)
    {

            Video video = videoService.getVideo(videoId);
            if(video == null)
            {
                throw new VideoNotFoundException(videoId);
            }
            Like like = new Like();
            like.setUserId(userId);
            like.setVideo(video);
            like.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            likeRepository.save(like);


    }
    public void removeLike(String userId,String videoId)
    {
        Specification<Like> specification= (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("userId"), userId);
        specification.and(((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("video").get("id"), videoId)));
       List<Like> likeExists = likeRepository.findAll(specification);
       if(!likeExists.isEmpty())
       {
           likeRepository.delete(likeExists.get(0));
       }
       else{
           throw new BadRequestException("Like doesn't exist");
       }
    }
}
