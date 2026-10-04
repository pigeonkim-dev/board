package com.pigeonkim.board.web.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 선행 주간 2일차 진단용. board P1 에서 지운다.
 *
 * 쓰는 이유 하나다 — IdP 가 준 sub 가 account.public_id 와 글자까지 같은지
 * 브라우저에서 눈으로 보는 것. 그게 이 주간의 핵심 전제다.
 *
 * 지금은 로그인 경로가 두 개다 (폼로그인 · IdP). 어느 쪽으로 들어왔는지도
 * 같이 찍어서, 로그인이 안 될 때 board 를 의심할지 IdP 를 의심할지 가린다.
 *
 * 주의: 이 응답에는 ID 토큰 클레임이 그대로 들어간다. 로컬 전용이다.
 */
@RestController
public class WhoAmIController {

    @GetMapping("/whoami")
    public Map<String, Object> whoami(Authentication authentication) {

        Map<String, Object> out = new LinkedHashMap<>();

        if (authentication == null || !authentication.isAuthenticated()) {
            out.put("authenticated", false);
            out.put("hint", "로그인 후 다시 열어보라. /oauth2/authorization/board 가 IdP 경로다.");
            return out;
        }

        out.put("authenticated", true);

        // getName() 이 곧 sub 다. IdP 쪽 AccountPrincipal.getUsername() 이
        // publicId.toString() 을 돌려주므로, IdP 로 들어왔다면 이 값이 public_id 다.
        // 폼로그인으로 들어왔다면 이메일이 찍힌다 — 그 차이로 경로를 구분한다.
        out.put("name", authentication.getName());

        // 어느 경로로 들어왔는지. OidcUser 면 IdP, CustomUserDetails 면 폼로그인.
        out.put("principalType", authentication.getPrincipal().getClass().getSimpleName());
        out.put("authenticationType", authentication.getClass().getSimpleName());

        out.put("authorities", authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        // IdP 로 들어온 경우에만 토큰 속을 들여다본다.
        if (authentication.getPrincipal() instanceof OidcUser oidcUser) {
            Map<String, Object> token = new LinkedHashMap<>();
            token.put("sub", oidcUser.getSubject());
            token.put("issuer", String.valueOf(oidcUser.getIssuer()));
            // 3-3 항목 확인용 — tokenCustomizer 가 ACCESS_TOKEN 일 때만 name 을 넣으므로
            // 여기(ID 토큰)에는 안 들어있을 것이다. 비어 있는 것이 정상이다.
            token.put("idTokenClaims", oidcUser.getIdToken().getClaims().keySet());
            token.put("userInfoClaims", oidcUser.getUserInfo() == null
                    ? "(userInfo 없음 — /userinfo 호출이 안 됐거나 401)"
                    : oidcUser.getUserInfo().getClaims().keySet());
            out.put("idp", token);
        }

        return out;
    }
}
