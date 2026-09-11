package com.pigeonkim.board.web.security;

import com.pigeonkim.board.web.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.http.HttpMethod;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 로그인 상태(SecurityContext)를 어디에 보관할지 정하는 물건.
     *
     * 원래 HttpSecurity 가 내부적으로 하나 만들어 쓰는데, 그러면 애플리케이션 코드에서
     * 주입받을 수가 없다. 닉네임을 바꾼 뒤 세션의 인증 정보를 갱신하려면
     * 컨트롤러도 "같은" 리포지토리를 봐야 하므로 빈으로 꺼내 놓는다.
     *
     * 두 개를 묶는 이유는 이것이 Spring Security 의 기본값과 같은 구성이기 때문이다.
     *   RequestAttribute...  현재 요청 안에서만 쓰는 임시 보관 (비동기·stateless 대비)
     *   HttpSession...       실제로 세션에 저장. 다음 요청에서도 로그인이 유지되는 이유
     * 하나만 쓰면 기본 동작 일부를 잃는다.
     */
    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new DelegatingSecurityContextRepository(
                new RequestAttributeSecurityContextRepository(),
                new HttpSessionSecurityContextRepository()
        );
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 필터가 쓰는 리포지토리를 위에서 만든 빈으로 지정한다.
                // 이걸 빼면 필터는 자기가 만든 것을, 컨트롤러는 빈을 보게 되어
                // 서로 다른 곳에 읽고 쓴다.
                .securityContext(context -> context
                        .securityContextRepository(securityContextRepository()))

                .authorizeHttpRequests(auth -> auth
                        // 1. 공개 경로
                        .requestMatchers("/", "/member/signup", "/member/login",
                                "/css/**", "/js/**", "/images/**").permitAll()

                        // 2. 게시글 목록/상세 — 비로그인 허용 (숫자 ID만 매칭)
                        .requestMatchers(HttpMethod.GET, "/board/posts").permitAll()
                        .requestMatchers(HttpMethod.GET, "/board/posts/{id:\\d+}").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/.well-known/**").permitAll()

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