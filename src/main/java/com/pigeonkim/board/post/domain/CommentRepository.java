package com.pigeonkim.board.post.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c JOIN FETCH c.author WHERE c.post.id = :postId AND c.status = :status ORDER BY c.createdAt ASC")
    List<Comment> findActiveCommentsByPostId(@Param("postId") Long postId, @Param("status") CommentStatus status);

    long countByPostIdAndStatus(Long postId, CommentStatus status);
}