package com.pigeonkim.board.web.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.savedrequest.RequestCache;

// TODO 공통-5  부품을 web-common 의 SecurityCommonConfig 로 보낸 뒤 이 클래스는 '규칙' 만 남긴다.
//   ① securityContextRepository() @Bean 메서드와 private oidcLogoutSuccessHandler(...) 를 지운다 (공통-3·4 로 옮긴 것).
//   ② 필드로 받는다:  private final SecurityContextRepository securityContextRepository;
//                    private final LogoutSuccessHandler oidcLogoutSuccessHandler;
//   ③ .securityContextRepository(securityContextRepository())  →  (securityContextRepository)      괄호가 빠진다. 호출이 아니라 필드다
//      .logoutSuccessHandler(oidcLogoutSuccessHandler(clientRegistrationRepository))  →  (oidcLogoutSuccessHandler)
//   ④ filterChain 파라미터의 ClientRegistrationRepository 는 이제 안 쓰니 뺀다. 안 쓰는 import 도 정리.
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final BoardOidcUserService boardOidcUserService;
    private final SignupRequiredAccessDeniedHandler signupRequiredAccessDeniedHandler;
    private final SignupRedirectSuccessHandler signupRedirectSuccessHandler;

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new DelegatingSecurityContextRepository(
                new RequestAttributeSecurityContextRepository(),
                new HttpSessionSecurityContextRepository()
        );
    }

    private OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler(
            ClientRegistrationRepository clientRegistrationRepository) {

        OidcClientInitiatedLogoutSuccessHandler handler = new OidcClientInitiatedLogoutSuccessHandler(
                clientRegistrationRepository
        );

        handler.setPostLogoutRedirectUri("{baseUrl}/");
        return handler;

    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           ClientRegistrationRepository clientRegistrationRepository,
                                           RequestCache requestCache) throws Exception {
        http
                .securityContext(context -> context
                        .securityContextRepository(securityContextRepository()))
                .requestCache(cache ->
                        cache.requestCache(requestCache))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login",
                                "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/board/posts").permitAll()
                        .requestMatchers(HttpMethod.GET, "/board/posts/{id:\\d+}").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/.well-known/**").permitAll()
                        .requestMatchers("/signup").authenticated()
                        .requestMatchers("/board/**").hasRole("USER")
                        .anyRequest().hasRole("USER")
                )
                .oauth2Login(oauth -> oauth
                        .loginPage("/login")
                        .successHandler(signupRedirectSuccessHandler)
                        .permitAll()
                        .userInfoEndpoint(userinfo ->
                                userinfo.oidcUserService(boardOidcUserService))
                )
                .exceptionHandling(ex ->
                        ex.accessDeniedHandler(signupRequiredAccessDeniedHandler))
                .logout(logout -> logout
                        .logoutSuccessHandler(oidcLogoutSuccessHandler(clientRegistrationRepository))
                        .permitAll()
                );

        return http.build();
    }
}