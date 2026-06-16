package com.pigeonkim.paymentshop.board.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p JOIN FETCH p.author WHERE p.status = 'ACTIVE' ORDER BY p.createdAt DESC")
    Page<Post> findActivePosts(Pageable pageable);

    @Query("SELECT p FROM Post p JOIN FETCH p.author WHERE p.id = :id AND p.status = 'ACTIVE'")
    Optional<Post> findActiveById(Long id);
}