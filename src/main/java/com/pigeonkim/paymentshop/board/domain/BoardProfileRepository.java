package com.pigeonkim.paymentshop.board.domain;

import com.pigeonkim.paymentshop.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoardProfileRepository extends JpaRepository<BoardProfile, Long> {

    Optional<BoardProfile> findByMember(Member member);

    boolean existsByMember(Member member);

    boolean existsByNickname(String nickname);
}
