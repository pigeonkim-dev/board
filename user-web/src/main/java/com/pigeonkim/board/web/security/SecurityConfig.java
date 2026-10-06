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

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final  BoardOidcUserService boardOidcUserService;
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
            ClientRegistrationRepository clientRegistrationRepository    ) {

        OidcClientInitiatedLogoutSuccessHandler handler = new OidcClientInitiatedLogoutSuccessHandler(
                clientRegistrationRepository
        );

        handler.setPostLogoutRedirectUri("{baseUrl}/");
        return handler;

    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           ClientRegistrationRepository clientRegistrationRepository) throws Exception {
        http
                .securityContext(context -> context
                        .securityContextRepository(securityContextRepository()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/member/login",
                                "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/board/posts").permitAll()
                        .requestMatchers(HttpMethod.GET, "/board/posts/{id:\\d+}").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/.well-known/**").permitAll()
                        // TODO 9-1  "/whoami" 를 지운다. 진단용 컨트롤러를 없앴다 → .requestMatchers("/signup").authenticated()
                        .requestMatchers("/signup", "/whoami").authenticated()
                        .requestMatchers("/board/**").hasRole("USER")
                        .anyRequest().hasRole("USER")
                )
                .oauth2Login(oauth -> oauth
                        .loginPage("/member/login")
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