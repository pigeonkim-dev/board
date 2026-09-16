package com.pigeonkim.board.service;

import com.pigeonkim.board.domain.entity.Member;
import com.pigeonkim.board.domain.entity.Profile;
import com.pigeonkim.board.exception.BusinessException;
import com.pigeonkim.board.exception.ErrorCode;
import com.pigeonkim.board.repository.MemberRepository;
import com.pigeonkim.board.domain.MemberRole;
import com.pigeonkim.board.repository.ProfileRepository;
import com.pigeonkim.board.service.command.SignupCommand;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final ProfileRepository profileRepository;

    @Transactional
    public void signup(SignupCommand signupCommand){

        if (memberRepository.findByEmail(signupCommand.getEmail()).isPresent()) {
            throw new BusinessException(ErrorCode.EMAIL_DUPLICATED);
        }

        if (profileRepository.existsByNickname(signupCommand.getNickname())) {
            throw new BusinessException(ErrorCode.NICKNAME_DUPLICATED);
        }

        String encodedPassword = passwordEncoder.encode(signupCommand.getPassword());

        Member member = Member.builder()
                .email(signupCommand.getEmail())
                .password(encodedPassword)
                .role(MemberRole.USER)
                .build();

        memberRepository.save(member);

        Profile profile = Profile.builder()
                .member(member)
                .nickname(signupCommand.getNickname())
                .build();

        profileRepository.save(profile);

    }
}
