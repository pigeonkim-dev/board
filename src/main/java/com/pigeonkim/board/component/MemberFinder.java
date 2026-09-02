package com.pigeonkim.board.component;

import com.pigeonkim.board.domain.entity.Member;
import com.pigeonkim.board.exception.NotFoundException;
import com.pigeonkim.board.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 이메일로 회원을 찾는 조회 전용 컴포넌트.
 *
 * PostService·CommentService 에 똑같이 있던 private getMember 를 한 자리로 모은다.
 * MemberService 에 두지 않는 이유: 서비스가 서비스를 의존하기 시작하면
 * B0-10 모듈 분리에서 PasswordEncoder 빈까지 딸려 온다.
 */
@Component
@RequiredArgsConstructor
public class MemberFinder {

    private final MemberRepository memberRepository;

    public Member getByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));
    }
}