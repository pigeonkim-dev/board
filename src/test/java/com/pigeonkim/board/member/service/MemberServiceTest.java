package com.pigeonkim.board.member.service;

import com.pigeonkim.board.member.domain.Member;
import com.pigeonkim.board.member.domain.MemberRepository;
import com.pigeonkim.board.member.dto.SignupRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MemberService memberService;

    @Test
    public  void signup_성공(){
        // given
        SignupRequest request = new SignupRequest("test@test.com", "1234", "테스트", "라쿤");
        given(memberRepository.findByEmail(request.getEmail())).willReturn(Optional.empty());
        given(memberRepository.existsByNickname(request.getNickname())).willReturn(false);
        given(passwordEncoder.encode(request.getPassword())).willReturn("encodedPassword");

        // when
        memberService.signup(request);

        // then
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    public void signup_이메일중복_예외(){
        // given
        SignupRequest request = new SignupRequest("test@test.com", "1234", "테스트", "라쿤");
        given(memberRepository.findByEmail(request.getEmail())).willReturn(Optional.of(mock(Member.class)));

        // when & then
        assertThrows(IllegalArgumentException.class, () -> memberService.signup(request));
    }

    @Test
    public void signup_닉네임중복_예외(){
        // given
        SignupRequest request = new SignupRequest("test@test.com", "1234", "테스트", "라쿤");
        given(memberRepository.findByEmail(request.getEmail())).willReturn(Optional.empty());
        given(memberRepository.existsByNickname(request.getNickname())).willReturn(true);

        // when & then
        assertThrows(IllegalArgumentException.class, () -> memberService.signup(request));
    }

}
