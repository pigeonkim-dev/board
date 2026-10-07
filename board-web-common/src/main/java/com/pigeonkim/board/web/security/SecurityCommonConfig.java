package com.pigeonkim.board.web.security;

import org.springframework.context.annotation.Configuration;

/**
 * 두 사이트가 같이 쓰는 시큐리티 '부품'. 규칙(어느 주소를 누가 보나)은 사이트마다 자기 SecurityConfig 에 둔다.
 */
@Configuration
public class SecurityCommonConfig {

    // TODO 공통-3  user-web 의 SecurityConfig 에서 securityContextRepository() @Bean 메서드를 통째로 옮겨 온다 (import 넷 포함).

    // TODO 공통-4  user-web 의 SecurityConfig 에 있는 private oidcLogoutSuccessHandler(...) 를 여기로 옮기되 @Bean 으로 바꾼다.
    //   @Bean
    //   public LogoutSuccessHandler oidcLogoutSuccessHandler(ClientRegistrationRepository clientRegistrationRepository) { ...같은 몸통... }
    //   반환 타입을 인터페이스 LogoutSuccessHandler 로 — 쓰는 쪽은 "로그아웃 뒤에 무엇을 하는 것" 만 알면 된다.
    //   import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
    //   ClientRegistrationRepository 는 각 앱의 yaml 등록에서 생긴 빈이 들어온다 — 그래서 user-web 은 board, admin-web 은 board-admin 으로 로그아웃된다.
}
