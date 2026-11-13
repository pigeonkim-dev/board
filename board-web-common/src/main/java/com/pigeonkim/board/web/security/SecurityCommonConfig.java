package com.pigeonkim.board.web.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OidcBackChannelLogoutHandler;
import org.springframework.security.oauth2.client.oidc.session.InMemoryOidcSessionRegistry;
import org.springframework.security.oauth2.client.oidc.session.OidcSessionRegistry;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

/**
 * 두 사이트가 같이 쓰는 시큐리티 '부품'. 규칙(어느 주소를 누가 보나)은 사이트마다 자기 SecurityConfig 에 둔다.
 */
@Configuration
public class SecurityCommonConfig {
    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new DelegatingSecurityContextRepository(
                new RequestAttributeSecurityContextRepository(),
                new HttpSessionSecurityContextRepository()
        );
    }

    @Bean
    public OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler(
            ClientRegistrationRepository clientRegistrationRepository) {

        OidcClientInitiatedLogoutSuccessHandler handler = new OidcClientInitiatedLogoutSuccessHandler(
                clientRegistrationRepository
        );

        handler.setPostLogoutRedirectUri("{baseUrl}/");
        return handler;

    }

    @Bean
    public OidcSessionRegistry oidcSessionRegistry() {
        return new InMemoryOidcSessionRegistry();
    }

    @Bean
    public OidcBackChannelLogoutHandler oidcBackChannelLogoutHandler(OidcSessionRegistry oidcSessionRegistry,
                                                                     @Value("${server.servlet.session.cookie.name}") String sessionCookieName) {
        OidcBackChannelLogoutHandler handler = new OidcBackChannelLogoutHandler(oidcSessionRegistry);
        handler.setSessionCookieName(sessionCookieName);

        return handler;
    }
}
