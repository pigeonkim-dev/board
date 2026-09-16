package com.pigeonkim.board.service;

import com.pigeonkim.board.domain.entity.Member;
import com.pigeonkim.board.domain.entity.Profile;
import com.pigeonkim.board.exception.BusinessException;
import com.pigeonkim.board.exception.ErrorCode;
import com.pigeonkim.board.repository.MemberRepository;
import com.pigeonkim.board.repository.ProfileRepository;
import com.pigeonkim.board.service.command.SignupCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ProfileRepository profileRepository;

    @InjectMocks
    private MemberService memberService;

    @Test
    public void signup_성공() {
        // given
        SignupCommand signupCommand = SignupCommand.of("test@test.com", "1234", "라쿤");

        given(memberRepository.findByEmail(signupCommand.getEmail())).willReturn(Optional.empty());
        given(profileRepository.existsByNickname(signupCommand.getNickname())).willReturn(false);
        given(passwordEncoder.encode(signupCommand.getPassword())).willReturn("encodedPassword");

        // when
        memberService.signup(signupCommand);

        // then
        verify(memberRepository, times(1)).save(any(Member.class));
        verify(profileRepository, times(1)).save(any(Profile.class));
    }

    @Test
    public void signup_이메일중복_예외() {
        // given
        SignupCommand signupCommand = SignupCommand.of("test@test.com", "1234", "라쿤");
        given(memberRepository.findByEmail(signupCommand.getEmail())).willReturn(Optional.of(mock(Member.class)));

        // when & then
        BusinessException e = assertThrows(BusinessException.class, () -> memberService.signup(signupCommand));

        assertEquals(ErrorCode.EMAIL_DUPLICATED, e.getErrorCode());
    }

    @Test
    public void signup_닉네임중복_예외() {
        // given
        SignupCommand signupCommand = SignupCommand.of("test@test.com", "1234", "라쿤");
        given(memberRepository.findByEmail(signupCommand.getEmail())).willReturn(Optional.empty());
        given(profileRepository.existsByNickname(signupCommand.getNickname())).willReturn(true);

        // when & then
        BusinessException e = assertThrows(BusinessException.class, () -> memberService.signup(signupCommand));

        assertEquals(ErrorCode.NICKNAME_DUPLICATED, e.getErrorCode());
    }

}
