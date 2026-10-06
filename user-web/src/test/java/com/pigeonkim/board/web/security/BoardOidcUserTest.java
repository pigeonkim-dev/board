package com.pigeonkim.board.web.security;

import com.pigeonkim.board.domain.MemberRole;
import com.pigeonkim.board.domain.entity.Member;
import com.pigeonkim.board.domain.entity.Profile;
import com.pigeonkim.board.domain.MemberRole;
import com.pigeonkim.board.domain.entity.Member;
import com.pigeonkim.board.domain.entity.Profile;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 순수 JUnit. 스프링 없음. BoardOidcUser 가 role 을 권한 문자열로 바꾸는 규칙만 본다.
 */
class BoardOidcUserTest {

    @Test
    void ofRegistered_ADMIN이면_ROLE_ADMIN() {

        Member member = Member.builder().role(MemberRole.ADMIN).publicId(UUID.randomUUID()).build();
        Profile profile = Profile.builder().member(member).nickname("admin").build();
        BoardOidcUser user = BoardOidcUser.ofRegistered(TestUsers.idpUser(member.getPublicId()), member, profile);

        assertTrue(user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
        assertEquals(member.getPublicId().toString(), user.getName());
    }

    @Test
    void ofUnregistered_권한이_비어있다() {
        
        UUID uuid = UUID.randomUUID();
        BoardOidcUser user = TestUsers.unregisteredUser(uuid);

        assertTrue(user.getAuthorities().isEmpty());
        assertFalse(user.isRegistered());
        assertEquals(user.getName(), uuid.toString());
    }
}
