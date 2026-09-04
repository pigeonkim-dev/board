package com.pigeonkim.board.repository;

import com.pigeonkim.board.domain.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 지금은 save 만 쓴다. JpaRepository 가 이미 준다.
 * <p>
 * 조회 메서드는 B0-9b 에서 실제로 필요해질 때 더한다.
 * 미리 만들지 않는 이유는 오늘 아침 Member.name 을 지운 이유와 같다 —
 * 아무도 안 쓰는 것은 만들지 않는다.
 */
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByMemberEmail(String email);
}
