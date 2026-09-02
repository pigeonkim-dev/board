package com.pigeonkim.board.service;

import com.pigeonkim.board.domain.entity.Member;
import com.pigeonkim.board.exception.DuplicateException;
import com.pigeonkim.board.repository.MemberRepository;
import com.pigeonkim.board.web.dto.SignupRequest;
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
    public void signup_성공() {
        // given
        SignupRequest request = new SignupRequest("test@test.com", "1234", "라쿤");
        given(memberRepository.findByEmail(request.getEmail())).willReturn(Optional.empty());
        given(memberRepository.existsByNickname(request.getNickname())).willReturn(false);
        given(passwordEncoder.encode(request.getPassword())).willReturn("encodedPassword");

        // when
        memberService.signup(request);

        // then
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    public void signup_이메일중복_예외() {
        // given
        SignupRequest request = new SignupRequest("test@test.com", "1234", "라쿤");
        given(memberRepository.findByEmail(request.getEmail())).willReturn(Optional.of(mock(Member.class)));

        // when & then
        assertThrows(DuplicateException.class, () -> memberService.signup(request));
    }

    @Test
    public void signup_닉네임중복_예외() {
        // given
        SignupRequest request = new SignupRequest("test@test.com", "1234", "라쿤");
        given(memberRepository.findByEmail(request.getEmail())).willReturn(Optional.empty());
        given(memberRepository.existsByNickname(request.getNickname())).willReturn(true);

        // when & then
        assertThrows(DuplicateException.class, () -> memberService.signup(request));
    }

}
