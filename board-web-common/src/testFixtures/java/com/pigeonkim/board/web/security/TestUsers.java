package com.pigeonkim.board.web.security;

import com.pigeonkim.board.domain.entity.Member;
import com.pigeonkim.board.domain.entity.Profile;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;

/**
 * 테스트용 "로그인한 사람" 공장.
 *
 * IdP 는 부르지 않는다. IdP 가 로그인을 끝내고 돌려준 '다음 상태'(sub 가 이 UUID 인 사람이 로그인되어 있다)를
 * 바로 만든다. 두 층이다.
 *   - ...User(...)  : BoardOidcUser 객체. 서비스 테스트처럼 객체 자체가 필요할 때
 *   - unregistered / registered : 그 객체를 요청에 붙이는 RequestPostProcessor.
 *                                 mockMvc.perform(get("/signup").with(TestUsers.unregistered(id))) 처럼 쓴다
 */
public final class TestUsers {

    private TestUsers() {
    }

    /** IdP 가 발급했을 법한 ID 토큰을 손으로 만든다. 테스트는 서명을 검사하지 않으므로 값은 아무거나. */
    public static OidcUser idpUser(UUID publicId) {
        OidcIdToken idToken = OidcIdToken.withTokenValue("test-token")
                .subject(publicId.toString())                 // sub = public_id. BoardOidcUser 가 여기서 UUID 를 꺼낸다
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(600))
                .build();
        return new DefaultOidcUser(List.of(), idToken);      // Spring 의 기본 OidcUser 구현. 이름 속성은 기본값 "sub"
    }

    /** 가입 전 사람. 권한이 비어 hasRole("USER") 에 걸리고 AccessDeniedHandler 로 간다. */
    public static BoardOidcUser unregisteredUser(UUID publicId) {
        return BoardOidcUser.ofUnregistered(idpUser(publicId));
    }

    /** 가입한 사람. DB 에 저장한 Member·Profile 을 받아 실제 principal 과 같은 모양을 만든다. */
    public static BoardOidcUser registeredUser(Member member, Profile profile) {
        return BoardOidcUser.ofRegistered(idpUser(member.getPublicId()), member, profile);
    }

    public static RequestPostProcessor unregistered(UUID publicId) {
        return oidcLogin().oidcUser(unregisteredUser(publicId));
    }

    public static RequestPostProcessor registered(Member member, Profile profile) {
        return oidcLogin().oidcUser(registeredUser(member, profile));
    }
}
