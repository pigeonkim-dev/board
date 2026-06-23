package com.pigeonkim.board.post.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p JOIN FETCH p.author WHERE p.status = :status ORDER BY p.createdAt DESC")
    Page<Post> findActivePosts(@Param("status") PostStatus status, Pageable pageable);

    @Query("SELECT p FROM Post p JOIN FETCH p.author WHERE p.id = :id AND p.status = :status")
    Optional<Post> findActiveById(@Param("id") Long id, @Param("status") PostStatus status);
}