package com.pigeonkim.paymentshop.board.service;

import com.pigeonkim.paymentshop.board.domain.BoardProfile;
import com.pigeonkim.paymentshop.board.domain.BoardProfileRepository;
import com.pigeonkim.paymentshop.board.dto.BoardProfileRequest;
import com.pigeonkim.paymentshop.board.exception.NicknameRequiredException;
import com.pigeonkim.paymentshop.member.domain.Member;
import com.pigeonkim.paymentshop.member.domain.MemberRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardProfileService {
    private final BoardProfileRepository boardProfileRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void createProfile(String email, BoardProfileRequest request) {
        // 1. Member 조회
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 2. 이미 프로필 있는지 체크
        if (boardProfileRepository.existsByMember(member)) {
            throw new IllegalArgumentException("이미 프로필이 존재합니다.");
        }

        // 3. 닉네임 중복 체크
        if (boardProfileRepository.existsByNickname(request.getNickname())) {
            throw new IllegalArgumentException("이미 사용중인 닉네임입니다.");
        }

        // 4. BoardProfile 생성 후 저장
        BoardProfile profile = BoardProfile.builder()
                .member(member)
                .nickname(request.getNickname())
                .build();

        boardProfileRepository.save(profile);
    }

    @Transactional
    public void updateProfile(String email, BoardProfileRequest request) {

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        BoardProfile profile = boardProfileRepository.findByMember(member)
                .orElseThrow(() -> new IllegalArgumentException("프로필이 존재하지 않습니다."));

        boolean isDuplicate = boardProfileRepository.existsByNickname(request.getNickname())
                && !profile.getNickname().equals(request.getNickname());
        if (isDuplicate) {
            throw new IllegalArgumentException("이미 사용중인 닉네임입니다.");
        }

        profile.changeNickname(request.getNickname());
    }

    @Transactional(readOnly = true)
    public BoardProfile getProfile(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        return boardProfileRepository.findByMember(member)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public BoardProfile requireProfile(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        return boardProfileRepository.findByMember(member)
                .orElseThrow(() -> new NicknameRequiredException("닉네임 설정이 필요합니다."));
    }
}
