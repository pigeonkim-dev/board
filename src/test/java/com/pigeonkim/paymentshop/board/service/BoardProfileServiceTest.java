package com.pigeonkim.paymentshop.board.service;

import com.pigeonkim.paymentshop.board.domain.BoardProfile;
import com.pigeonkim.paymentshop.board.domain.BoardProfileRepository;
import com.pigeonkim.paymentshop.board.dto.BoardProfileRequest;
import com.pigeonkim.paymentshop.board.exception.NicknameRequiredException;
import com.pigeonkim.paymentshop.member.domain.Member;
import com.pigeonkim.paymentshop.member.domain.MemberRepository;
import com.pigeonkim.paymentshop.member.domain.MemberRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class BoardProfileServiceTest {

    @Mock
    private BoardProfileRepository boardProfileRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private BoardProfileService boardProfileService;

    @Test
    void createProfile_성공() {
        // given - 데이터 준비
        Member member = Member.builder()
                .email("test@test.com")
                .password("encoded")
                .name("테스트")
                .role(MemberRole.USER)
                .build();

        BoardProfileRequest request = new BoardProfileRequest();
        request.setNickname("raccoon");

        // Mock 동작 설정: "이렇게 호출하면 이걸 반환해라"
        given(memberRepository.findByEmail("test@test.com")).willReturn(Optional.of(member));
        given(boardProfileRepository.existsByMember(member)).willReturn(false);
        given(boardProfileRepository.existsByNickname("raccoon")).willReturn(false);

        // when - 테스트 대상 호출
        boardProfileService.createProfile("test@test.com", request);

        // then - 검증
        verify(boardProfileRepository, times(1)).save(any(BoardProfile.class));
    }

    @Test
    void createProfile_이미존재하는프로필_예외() {
        // given - 데이터 준비
        Member member = Member.builder()
                .email("test@test.com")
                .password("encoded")
                .name("테스트")
                .role(MemberRole.USER)
                .build();

        BoardProfileRequest request = new BoardProfileRequest();
        request.setNickname("raccoon");

        // Mock 동작 설정: "이렇게 호출하면 이걸 반환해라"
        given(memberRepository.findByEmail("test@test.com")).willReturn(Optional.of(member));
        given(boardProfileRepository.existsByMember(member)).willReturn(true);

        assertThrows(IllegalArgumentException.class, () -> boardProfileService.createProfile(member.getEmail(), request));
    }

    @Test
    void createProfile_닉네임중복_예외() {

        Member member = Member.builder()
                .email("test@test.com")
                .password("encoded")
                .name("테스트")
                .role(MemberRole.USER)
                .build();

        BoardProfileRequest request = new BoardProfileRequest();
        request.setNickname("raccoon");

        // Mock 동작 설정: "이렇게 호출하면 이걸 반환해라"
        given(memberRepository.findByEmail("test@test.com")).willReturn(Optional.of(member));
        given(boardProfileRepository.existsByMember(member)).willReturn(false);
        given(boardProfileRepository.existsByNickname("raccoon")).willReturn(true);

        assertThrows(IllegalArgumentException.class, () -> boardProfileService.createProfile(member.getEmail(), request));

    }

    @Test
    void requireProfile_프로필없음_예외() {
        // given
        Member member = Member.builder()
                .email("test@test.com")
                .password("encoded")
                .name("테스트")
                .role(MemberRole.USER)
                .build();

        given(memberRepository.findByEmail("test@test.com")).willReturn(Optional.of(member));
        given(boardProfileRepository.findByMember(member)).willReturn(Optional.empty());

        // when & then
        assertThrows(NicknameRequiredException.class,
                () -> boardProfileService.requireProfile(member.getEmail()));
    }

    @Test
    void updateProfile_성공() {
        // given
        Member member = Member.builder()
                .email("test@test.com")
                .password("encoded")
                .name("테스트")
                .role(MemberRole.USER)
                .build();

        BoardProfile existingProfile = BoardProfile.builder()
                .member(member)
                .nickname("oldNickname")
                .build();

        BoardProfileRequest request = new BoardProfileRequest();
        request.setNickname("newNickname");

        given(memberRepository.findByEmail("test@test.com")).willReturn(Optional.of(member));
        given(boardProfileRepository.findByMember(member)).willReturn(Optional.of(existingProfile));
        given(boardProfileRepository.existsByNickname("newNickname")).willReturn(false);

        // when
        boardProfileService.updateProfile(member.getEmail(), request);

        // then - 닉네임이 실제로 바뀌었는지 검증
        assertEquals("newNickname", existingProfile.getNickname());
    }
}