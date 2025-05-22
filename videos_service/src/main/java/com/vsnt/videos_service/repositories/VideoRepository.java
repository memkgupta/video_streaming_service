package com.vsnt.videos_service.repositories;

import com.vsnt.videos_service.entities.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface VideoRepository extends JpaRepository<Video,String>, JpaSpecificationExecutor<Video> {

}
