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
     * <p>
     * 원래 HttpSecurity 가 내부적으로 하나 만들어 쓰는데, 그러면 애플리케이션 코드에서
     * 주입받을 수가 없다. 닉네임을 바꾼 뒤 세션의 인증 정보를 갱신하려면
     * 컨트롤러도 "같은" 리포지토리를 봐야 하므로 빈으로 꺼내 놓는다.
     * <p>
     * 두 개를 묶는 이유는 이것이 Spring Security 의 기본값과 같은 구성이기 때문이다.
     * RequestAttribute...  현재 요청 안에서만 쓰는 임시 보관 (비동기·stateless 대비)
     * HttpSession...       실제로 세션에 저장. 다음 요청에서도 로그인이 유지되는 이유
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
                .oauth2Login(oauth -> oauth
                        .loginPage("/member/login")
                        .defaultSuccessUrl("/")
                        .permitAll()
                )

                // oauth2Login 이 필터 두 개를 체인에 끼운다 (바이트코드로 확인한 등록 순서):
                //   2번  OAuth2AuthorizationRequestRedirectFilter
                //        /oauth2/authorization/board 를 받아 IdP 의 authorize 로 302
                //   5번  OAuth2LoginAuthenticationFilter
                //        /login/oauth2/code/board 로 돌아온 code 를 토큰으로 바꾸고
                //        access_token 으로 /userinfo 를 불러 OidcUser 를 만든다
                // 둘 다 인가 검사(9번 FilterSecurityInterceptor)보다 앞이라
                // authorizeHttpRequests 의 permitAll 목록을 건드리지 않아도 통과한다.
                //
                // loginPage 를 formLogin 과 같은 값으로 둔 이유:
                // 두 메커니즘이 각자 AuthenticationEntryPoint 를 등록한다. 다른 화면을
                // 가리키면 비로그인 접근 시 어디로 갈지가 갈린다. 그리고 oauth2Login 에
                // loginPage 를 안 주면 스프링이 DefaultLoginPageGeneratingFilter 로
                // 제공자 선택 화면을 만들어 끼운다 — 우리는 화면이 이미 있다.
                //
                // defaultSuccessUrl("/") 에 대해 (한 인자 버전):
                // 내부적으로 defaultSuccessUrl("/", false) 를 부른다. 핸들러는
                // SavedRequestAwareAuthenticationSuccessHandler 이고 alwaysUse 가 false 다.
                // 즉 "로그인 안 된 사람이 원래 가려던 주소"(SavedRequest) 가 있으면 그쪽이
                // 이기고, 없을 때만 "/" 로 간다. 복원을 끄려면 둘째 인자에 true 를 준다.
                //
                // 폼로그인을 남겨둔 이유 (선행 주간 한정):
                // ① IdP 경로가 안 될 때 폼 경로가 되면 원인이 OAuth2 배선으로 좁혀진다
                // ② IdP 로 로그인하면 principal 이 DefaultOidcUser 라
                //    컨트롤러의 @AuthenticationPrincipal CustomUserDetails 가 조용히 null 이 된다.
                //    (AuthenticationPrincipalArgumentResolver 의 errorOnInvalidType 기본값 false)
                //    조회는 비로그인처럼 보이고 쓰기는 NPE 500 이다. 예정된 상태다.
                //    그래서 기존 기능 회귀 확인은 폼 경로로 한다.
                // 폼로그인은 board P1-7(10/21) 에서 지운다.
                //
                // userInfoEndpoint(...) 로 커스텀 OidcUserService 를 끼우는 자리가 여기지만
                // 그건 board P1-5(10/22) 다. 오늘은 기본 DefaultOidcUser 를 쓰고
                // /whoami 로 sub 를 눈으로 확인한다.

                .logout(logout -> logout
                        .logoutSuccessUrl("/member/login")
                        .permitAll()
                )
                .userDetailsService(customUserDetailsService);

        return http.build();
    }
}