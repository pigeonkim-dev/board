package com.pigeonkim.board.service;

import com.pigeonkim.board.domain.entity.Member;
import com.pigeonkim.board.exception.DuplicateException;
import com.pigeonkim.board.repository.MemberRepository;
import com.pigeonkim.board.domain.MemberRole;
import com.pigeonkim.board.web.dto.SignupRequest;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    @Transactional
    public void signup(SignupRequest request){

        if (memberRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateException("이미 사용중인 이메일입니다.");
        }

        if (memberRepository.existsByNickname(request.getNickname())) {
            throw new DuplicateException("이미 사용중인 닉네임입니다.");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        Member member = Member.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .nickname(request.getNickname())
                .role(MemberRole.USER)
                .build();

        memberRepository.save(member);

    }
}
