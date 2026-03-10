package com.vsnt.videos_service.repositories;

import com.vsnt.videos_service.entities.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LikeRepository extends JpaRepository<Like, Long>, JpaSpecificationExecutor<Like> {
}
