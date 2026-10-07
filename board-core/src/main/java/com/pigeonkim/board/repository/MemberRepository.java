package com.pigeonkim.board.repository;

import com.pigeonkim.board.domain.entity.Member;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByPublicId(UUID publicId);
}
