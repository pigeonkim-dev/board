package com.pigeonkim.board.web.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OidcBackChannelLogoutHandler;
import org.springframework.security.oauth2.client.oidc.session.OidcSessionRegistry;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * IdP 가 서버끼리 보내는 로그아웃 신호(logout token)를 받는 쪽.
 * 진짜 서명된 토큰으로 세션이 끊기는지는 로컬 IdP 로 끝에서 끝까지 확인한다. 여기서는 그 앞의 배선만 본다.
 */
@SpringBootTest
@AutoConfigureMockMvc
class BackChannelLogoutTest {

    private static final String BACK_CHANNEL_URI = "/logout/connect/back-channel/board";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OidcSessionRegistry oidcSessionRegistry;

    @Autowired
    private OidcBackChannelLogoutHandler oidcBackChannelLogoutHandler;

    @Test
    void 받는주소_CSRF토큰_없이_들어온다() throws Exception {
        mockMvc.perform(post(BACK_CHANNEL_URI))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 처리기와_로그인이_같은_세션목록을_쓴다() {

        Object registryInHandler = ReflectionTestUtils.getField(oidcBackChannelLogoutHandler, "sessionRegistry");
        assertSame(oidcSessionRegistry, registryInHandler);
    }

    @Test
    void 세션을_끊을때_BOARDSESSION_쿠키로_보낸다() {
        Object cookieNameName = ReflectionTestUtils.getField(oidcBackChannelLogoutHandler, "sessionCookieName");
        assertEquals("BOARDSESSION", cookieNameName);
    }
}
