package com.pigeonkim.board.service;

import com.pigeonkim.board.repository.MemberRepository;
import com.pigeonkim.board.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final ProfileRepository profileRepository;

}
