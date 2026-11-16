package com.pigeonkim.board.web.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import java.io.IOException;

/**
 * 메인(GET /)에 처음 온 익명 방문자를 조용한 로그인 시작 주소로 보낸다 (2026-11-13).
 * 시큐리티 필터 체인 안, AnonymousAuthenticationFilter 뒤에 선다. 그 자리여야 "지금 로그인한 사람인가"를 알 수 있다.
 * <p>
 * 빈(@Component)으로 만들지 않는다. 스프링 부트는 Filter 빈을 서블릿 컨테이너에도 따로 걸어서,
 * 시큐리티 체인 밖에서 한 번 더 돈다. SecurityConfig 에서 new 로 만들어 체인에만 넣는다.
 */
public class SilentLoginFilter extends OncePerRequestFilter {

    private static final RequestMatcher HOME = PathPatternRequestMatcher.pathPattern(HttpMethod.GET, "/");

    /**
     * 조용한 로그인 시작 주소. 예: /oauth2/authorization/board?prompt=none
     */
    private final String silentAuthorizationUri;

    public SilentLoginFilter(String authorizationUri) {
        this.silentAuthorizationUri = authorizationUri
                + "?" + SilentLogin.PROMPT_PARAMETER + "=" + SilentLogin.PROMPT_NONE;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = false;
        if (authentication != null) {
            isAuthenticated = new AuthenticationTrustResolverImpl().isAuthenticated(authentication);
        }

        if (!HOME.matches(request) ||
                isAuthenticated ||
                WebUtils.getCookie(request, SilentLogin.TRIED_COOKIE) != null) {

            filterChain.doFilter(request, response);
            return;
        }

        Cookie cookie = new Cookie(SilentLogin.TRIED_COOKIE, "1");
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(SilentLogin.TRIED_COOKIE_MAX_AGE_SECONDS);
        response.addCookie(cookie);

        request.getSession().setAttribute(SilentLogin.IN_PROGRESS_ATTRIBUTE, Boolean.TRUE);
        response.sendRedirect(request.getContextPath() + silentAuthorizationUri);
    }
}
