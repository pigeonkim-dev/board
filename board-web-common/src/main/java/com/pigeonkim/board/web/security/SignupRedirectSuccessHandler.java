package com.pigeonkim.board.web.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SignupRedirectSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    public SignupRedirectSuccessHandler() {
        setDefaultTargetUrl("/");
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session != null && session.getAttribute(SilentLogin.IN_PROGRESS_ATTRIBUTE) != null) {
            session.removeAttribute(SilentLogin.IN_PROGRESS_ATTRIBUTE);
            response.sendRedirect(request.getContextPath() + SilentLogin.RETURN_URL);
            return;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof BoardOidcUser user && !user.isRegistered()) {
            response.sendRedirect("/signup");
            return;
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
