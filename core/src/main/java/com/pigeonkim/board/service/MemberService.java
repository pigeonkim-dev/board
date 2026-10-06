package com.pigeonkim.board.service;

import com.pigeonkim.board.domain.MemberRole;
import com.pigeonkim.board.domain.entity.Member;
import com.pigeonkim.board.domain.entity.Profile;
import com.pigeonkim.board.exception.BusinessException;
import com.pigeonkim.board.exception.ErrorCode;
import com.pigeonkim.board.repository.MemberRepository;
import com.pigeonkim.board.repository.ProfileRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final ProfileRepository profileRepository;

    @Transactional
    public void completeSignup(UUID publicId, String nickname) {

        if (profileRepository.existsByNickname(nickname)) {
            throw new BusinessException(ErrorCode.NICKNAME_DUPLICATED);
        }

        Member member = Member.builder().role(MemberRole.USER).publicId(publicId).build();
        memberRepository.save(member);

        Profile profile = Profile.builder().member(member).nickname(nickname).build();
        profileRepository.save(profile);
    }

}
