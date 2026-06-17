package com.pigeonkim.paymentshop.member.service;

import com.pigeonkim.paymentshop.member.domain.Member;
import com.pigeonkim.paymentshop.member.domain.MemberRepository;
import com.pigeonkim.paymentshop.member.domain.MemberRole;
import com.pigeonkim.paymentshop.member.dto.SignupRequest;
import jakarta.transaction.Transactional;
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
            throw new IllegalArgumentException("이미 사용중인 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        Member member = Member.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .name(request.getName())
                .role(MemberRole.USER)
                .build();

        memberRepository.save(member);

    }
}
