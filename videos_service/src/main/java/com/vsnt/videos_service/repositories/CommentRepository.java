package com.vsnt.videos_service.repositories;

import com.vsnt.videos_service.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,String> , JpaSpecificationExecutor<Comment> {
    @Query("SELECT c.parentId, COUNT(c) FROM Comment c WHERE c.parentId IN :parentIds GROUP BY c.parentId")
    List<Object[]> countRepliesForParentIds(@Param("parentIds") List<String> parentIds);
}
