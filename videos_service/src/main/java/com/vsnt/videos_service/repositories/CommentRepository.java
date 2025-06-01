package com.vsnt.videos_service.repositories;

import com.vsnt.videos_service.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CommentRepository extends JpaRepository<Comment,String> , JpaSpecificationExecutor<Comment> {
}
