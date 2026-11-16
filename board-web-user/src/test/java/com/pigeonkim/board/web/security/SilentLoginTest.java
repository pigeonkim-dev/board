package com.pigeonkim.board.web.security;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 조용한 로그인(prompt=none). IdP 를 부르지 않고 board 쪽 판단만 본다.
 * IdP 가 정말 비밀번호 없이 돌려보내는지는 로컬 IdP 로 브라우저에서 확인한다.
 */
@SpringBootTest
@AutoConfigureMockMvc
class SilentLoginTest {

    private static final String SILENT_START = "/oauth2/authorization/board?prompt=none";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SilentLoginFailureHandler silentLoginFailureHandler;

    @Autowired
    private SignupRedirectSuccessHandler signupRedirectSuccessHandler;

    // ---- 시작: SilentLoginFilter ----

    @Test
    void 익명으로_메인에_처음_오면_조용한_로그인으로_보낸다() throws Exception {
        MvcResult result = mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SILENT_START))
                .andExpect(cookie().exists(SilentLogin.TRIED_COOKIE))
                .andExpect(cookie().maxAge(SilentLogin.TRIED_COOKIE, SilentLogin.TRIED_COOKIE_MAX_AGE_SECONDS))
                .andReturn();

        assertEquals(Boolean.TRUE, result.getRequest().getSession().getAttribute(SilentLogin.IN_PROGRESS_ATTRIBUTE));
    }

    @Test
    void 시도_표시가_있으면_메인을_그대로_보여준다() throws Exception {
        mockMvc.perform(get("/")
                .cookie(new Cookie(SilentLogin.TRIED_COOKIE, "1")))
                .andExpect(status().isOk());

    }

    @Test
    void 로그인한_사람은_메인을_그대로_본다() throws Exception {
        mockMvc.perform(get("/")
                        .with(TestUsers.unregistered(UUID.randomUUID())))
                .andExpect(status().isOk());
    }

    @Test
    void 메인이_아닌_공개_화면에서는_보내지_않는다() throws Exception {
        mockMvc.perform(get("/board/posts"))
                .andExpect(status().isOk())
                .andExpect(cookie().doesNotExist(SilentLogin.TRIED_COOKIE));
    }

    // ---- IdP 로 가는 요청: SilentLoginAuthorizationRequestResolver ----

    @Test
    void 조용한_시작_주소면_IdP_주소에_prompt_none을_붙인다() throws Exception {
        MvcResult result = mockMvc.perform(get(SILENT_START)).andReturn();

        assertTrue(result.getResponse().getHeader("Location").contains("prompt=none"));
    }

    @Test
    void 보통_로그인_주소에는_prompt를_붙이지_않는다() throws Exception {

        MvcResult result= mockMvc.perform(get("/oauth2/authorization/board")).andReturn();

        assertFalse(result.getResponse().getHeader("Location").contains("prompt="));
    }

    // ---- 돌아온 뒤: 실패·성공 처리기 (IdP 응답을 흉내 내기 어려워 처리기를 직접 부른다) ----

    @Test
    void 조용한_시도가_실패하면_메인으로_돌아가고_표시를_지운다() throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.getSession().setAttribute(SilentLogin.IN_PROGRESS_ATTRIBUTE, Boolean.TRUE);
        silentLoginFailureHandler.onAuthenticationFailure(request, response, new OAuth2AuthenticationException(new OAuth2Error("login_required")));

        assertEquals("/", response.getRedirectedUrl());
        assertNull(request.getSession().getAttribute(SilentLogin.IN_PROGRESS_ATTRIBUTE));
    }

    @Test
    void 보통_로그인이_실패하면_로그인_오류_화면으로_보낸다() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        silentLoginFailureHandler.onAuthenticationFailure(request, response, new OAuth2AuthenticationException(new OAuth2Error("login_required")));

        assertEquals("/login?error", response.getRedirectedUrl());

    }

    @Test
    void 조용히_로그인한_미가입자는_가입_화면이_아니라_메인에_둔다() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.getSession().setAttribute(SilentLogin.IN_PROGRESS_ATTRIBUTE, "1");

        TestingAuthenticationToken token = new TestingAuthenticationToken(TestUsers.unregisteredUser(UUID.randomUUID()), null);
        signupRedirectSuccessHandler.onAuthenticationSuccess(request, response, token);

        assertEquals("/", response.getRedirectedUrl());
    }

    @Test
    void 버튼으로_로그인한_미가입자는_지금처럼_가입_화면으로_보낸다() throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        TestingAuthenticationToken token = new TestingAuthenticationToken(TestUsers.unregisteredUser(UUID.randomUUID()), null);
        signupRedirectSuccessHandler.onAuthenticationSuccess(request, response, token);

        assertEquals("/signup", response.getRedirectedUrl());
    }
}
