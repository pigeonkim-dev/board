package com.pigeonkim.board.common.config;

import com.pigeonkim.board.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.http.HttpMethod;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // 1. 공개 경로
                        .requestMatchers("/", "/member/signup", "/member/login",
                                "/css/**", "/js/**", "/images/**").permitAll()

                        // 2. 게시글 목록/상세 — 비로그인 허용 (숫자 ID만 매칭)
                        .requestMatchers(HttpMethod.GET, "/board/posts").permitAll()
                        .requestMatchers(HttpMethod.GET, "/board/posts/{id:\\d+}").permitAll()

                        // 3. 관리자 전용
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // 4. 나머지 /board/** — 로그인 필수
                        .requestMatchers("/board/**").authenticated()

                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/member/login")
                        .defaultSuccessUrl("/")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/member/login")
                        .permitAll()
                )
                .userDetailsService(customUserDetailsService);

        return http.build();
    }
}