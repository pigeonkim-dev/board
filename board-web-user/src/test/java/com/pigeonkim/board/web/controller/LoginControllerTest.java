package com.pigeonkim.board.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * /login 은 중간 화면 없이 IdP 로 보내고, 실패했을 때만 안내 화면을 보여 준다.
 * 저장 여부는 Spring 이 세션에 쓰는 이름 SPRING_SECURITY_SAVED_REQUEST 로 본다.
 */
@SpringBootTest
@AutoConfigureMockMvc
class LoginControllerTest {

    private static final String SAVED_REQUEST = "SPRING_SECURITY_SAVED_REQUEST";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void login_바로_IdP로_보낸다() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/oauth2/authorization/board"));
    }

    @Test
    void login_실패하면_안내화면을_보여준다() throws Exception {
        mockMvc.perform(get("/login").param("error", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    void 로그인필요주소_돌아갈주소를_저장하고_login으로() throws Exception {
        MvcResult blocked = mockMvc.perform(get("/board/posts/new"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andReturn();

        assertNotNull(blocked.getRequest().getSession().getAttribute(SAVED_REQUEST));
    }

    @Test
    void 브라우저자동요청_돌아갈주소로_저장하지_않는다() throws Exception {
        MvcResult blocked = mockMvc.perform(get("/favicon.ico"))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        assertNull(blocked.getRequest().getSession().getAttribute(SAVED_REQUEST));
    }
}
