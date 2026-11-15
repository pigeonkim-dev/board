package com.pigeonkim.board.web.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

/**
 * IdP 로 보내는 로그인 요청을 만드는 쪽 (2026-11-13).
 * 만드는 일은 스프링 기본 구현(DefaultOAuth2AuthorizationRequestResolver)에 그대로 맡기고,
 * 로그인 시작 주소에 prompt=none 이 붙어 왔을 때만 결과에 prompt=none 을 더한다.
 */
@Component
public class SilentLoginAuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

    private final OAuth2AuthorizationRequestResolver delegate;

    public SilentLoginAuthorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository) {
        this.delegate = new DefaultOAuth2AuthorizationRequestResolver(
                clientRegistrationRepository,
                OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        return addPromptNoneIfAsked(request, delegate.resolve(request));
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        return addPromptNoneIfAsked(request, delegate.resolve(request, clientRegistrationId));
    }

    private OAuth2AuthorizationRequest addPromptNoneIfAsked(HttpServletRequest request,
                                                            OAuth2AuthorizationRequest authorizationRequest) {

        if (authorizationRequest == null) {
            return null;
        }

        if (SilentLogin.PROMPT_NONE.equals(request.getParameter(SilentLogin.PROMPT_PARAMETER))) {
            return OAuth2AuthorizationRequest.from(authorizationRequest)
                    .additionalParameters(
                            params ->
                                    params.put(SilentLogin.PROMPT_PARAMETER, SilentLogin.PROMPT_NONE)).build();
        }

        return authorizationRequest;
    }
}
