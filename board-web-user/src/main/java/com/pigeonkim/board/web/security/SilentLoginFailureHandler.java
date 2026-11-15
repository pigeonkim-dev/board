package com.pigeonkim.board.web.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 로그인 실패 처리 (2026-11-13).
 * 조용한 시도가 실패하면(IdP 가 login_required 로 돌려보내면) 아무 일 없던 것처럼 메인으로 돌려보낸다.
 * 그 밖의 실패는 지금까지와 같이 로그인 오류 화면(/login?error)으로 보낸다.
 */
@Component
public class SilentLoginFailureHandler implements AuthenticationFailureHandler {

    /** 지금까지의 동작. oauth2Login 이 따로 지정하지 않았을 때 쓰던 기본값과 같다 */
    private final AuthenticationFailureHandler defaultHandler =
            new SimpleUrlAuthenticationFailureHandler("/login?error");

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        HttpSession session = request.getSession(false);

        if (session != null && session.getAttribute(SilentLogin.IN_PROGRESS_ATTRIBUTE) != null){
            session.removeAttribute(SilentLogin.IN_PROGRESS_ATTRIBUTE);
            response.sendRedirect(request.getContextPath()+SilentLogin.RETURN_URL);
            return;
        }

        defaultHandler.onAuthenticationFailure(request, response, exception);
    }
}
