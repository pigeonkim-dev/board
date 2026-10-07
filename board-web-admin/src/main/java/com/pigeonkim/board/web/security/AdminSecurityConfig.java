package com.pigeonkim.board.web.security;

import org.springframework.context.annotation.Configuration;

/**
 * 관리자 사이트의 규칙. 부품(SecurityContextRepository, 로그아웃 핸들러, 핸들러 둘, BoardOidcUserService)은 web-common 이 준다.
 */
@Configuration
public class AdminSecurityConfig {
    // TODO admin-2  user-web 의 SecurityConfig(공통-5 까지 끝낸 모양)를 가져와 규칙만 바꾼다.
    //   공개 주소: "/login", "/css/**", "/error" 셋뿐. 관리자 사이트에 비로그인 화면은 없다.
    //   "/signup" 은 authenticated()  — P2-3 관리자 가입 화면이 쓴다
    //   anyRequest() 는 지금은 authenticated(). P2-2 에서 hasRole("ADMIN") 으로 조인다 — 지금 조이면 ADMIN 이 한 명도 없어 아무도 못 들어온다.
    //   oauth2Login / exceptionHandling / logout 은 user-web 과 같다.
}
