package com.pigeonkim.board.web.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoginRefresher {
    private final SecurityContextRepository securityContextRepository;
    private final BoardOidcUserService boardOidcUserService;

    public void refresh(HttpServletRequest request, HttpServletResponse response) {

        OAuth2AuthenticationToken current =
                (OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        BoardOidcUser newPrincipal = boardOidcUserService.reload((BoardOidcUser) current.getPrincipal());

        Authentication newAuthentication = new OAuth2AuthenticationToken(
                newPrincipal, newPrincipal.getAuthorities(), current.getAuthorizedClientRegistrationId());
        SecurityContext newContext = SecurityContextHolder.createEmptyContext();
        newContext.setAuthentication(newAuthentication);
        SecurityContextHolder.setContext(newContext);

        securityContextRepository.saveContext(newContext, request, response);
    }

}
