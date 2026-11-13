package com.pigeonkim.board.web.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.savedrequest.RequestCache;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final BoardOidcUserService boardOidcUserService;
    private final SignupRequiredAccessDeniedHandler signupRequiredAccessDeniedHandler;
    private final SignupRedirectSuccessHandler signupRedirectSuccessHandler;
    private final SecurityContextRepository securityContextRepository;
    private final LogoutSuccessHandler oidcLogoutSuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           RequestCache requestCache) throws Exception {
        http
                .securityContext(context -> context
                        .securityContextRepository(securityContextRepository))
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
                        .logoutSuccessHandler(oidcLogoutSuccessHandler)
                        .permitAll()

                ).oidcLogout(oidc ->
                        oidc.backChannel(Customizer.withDefaults()));

        return http.build();
    }
}