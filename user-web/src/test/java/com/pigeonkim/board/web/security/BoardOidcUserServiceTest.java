package com.pigeonkim.board.web.security;

import com.pigeonkim.board.domain.MemberRole;
import com.pigeonkim.board.domain.entity.Member;
import com.pigeonkim.board.domain.entity.Profile;
import com.pigeonkim.board.repository.MemberRepository;
import com.pigeonkim.board.repository.ProfileRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * "로그아웃 후 재로그인하면 가입 화면을 안 본다" 의 자동 테스트 판.
 * 진짜 loadUser 는 IdP 를 불러야 돌아서 테스트할 수 없다. 같은 로직인 reload 로 본다.
 * MockMvc 는 필요 없다 — 서비스 메서드를 직접 부른다.
 */
@SpringBootTest
@Transactional
class BoardOidcUserServiceTest {

    @Autowired
    private BoardOidcUserService boardOidcUserService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Test
    void reload_가입한뒤_registered가_true이고_닉네임과_권한이_실린다() {

        UUID uuid =  UUID.randomUUID();
        Member member = Member.builder().role(MemberRole.USER).publicId(uuid).build();
        Profile profile = Profile.builder().member(member).nickname("tester").build();
        memberRepository.save(member);
        profileRepository.save(profile);

        BoardOidcUser before = TestUsers.unregisteredUser(uuid);
        BoardOidcUser after = boardOidcUserService.reload(before);

        assertTrue(after.isRegistered());
        assertEquals("tester", after.getNickname());

        assertTrue(after.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    void reload_가입안한사람_그대로_unregistered() {
        UUID uuid =  UUID.randomUUID();

        BoardOidcUser before = TestUsers.unregisteredUser(uuid);
        BoardOidcUser after = boardOidcUserService.reload(before);

        assertFalse(after.isRegistered());
        assertTrue(after.getAuthorities().isEmpty());
    }
}
