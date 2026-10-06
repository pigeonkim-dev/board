package com.pigeonkim.board.web.controller;

import com.pigeonkim.board.domain.MemberRole;
import com.pigeonkim.board.domain.entity.Member;
import com.pigeonkim.board.domain.entity.Profile;
import com.pigeonkim.board.web.security.TestUsers;
import com.pigeonkim.board.repository.MemberRepository;
import com.pigeonkim.board.repository.ProfileRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

// 아래 static import 가 테스트 본문에서 쓰는 전부다.
//   get(...) / post(...)                      요청 만들기
//   status() / redirectedUrl() / view() / model()   응답 검사
//   csrf()                                    POST 에 CSRF 토큰 붙이기 (안 붙이면 403)

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 가입 흐름. 앱 전체를 띄우고(@SpringBootTest) 가짜 브라우저(MockMvc)로 요청한다. DB 는 board_test.
 *
 * @Transactional 이 클래스에 붙어 있으면 테스트 메서드 하나가 트랜잭션 하나이고, 끝나면 롤백된다.
 * 그래서 테스트끼리 데이터가 안 섞이고, 끝난 뒤 DB 가 비어 있다.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Test
    void 가입전_글쓰기화면_signup으로_리다이렉트() throws Exception {
        UUID uuid = UUID.randomUUID();
        mockMvc.perform(get("/board/posts/new").with(TestUsers.unregistered(uuid)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/signup"));
    }

    @Test
    void signup_닉네임제출_Member와Profile이_생기고_홈으로() throws Exception {
        UUID uuid = UUID.randomUUID();

        mockMvc.perform(post("/signup")
                        .with(TestUsers.unregistered(uuid))
                        .with(csrf())
                        .param("nickname", "tester"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        assertTrue(memberRepository.findByPublicId(uuid).isPresent());
        assertEquals("tester", profileRepository.findByMemberPublicId(uuid).orElseThrow().getNickname());
    }

    @Test
    void signup_중복닉네임_signup화면에_머물고_전역에러() throws Exception {

        Member member = Member.builder().role(MemberRole.USER).publicId(UUID.randomUUID()).build();
        Profile profile = Profile.builder().member(member).nickname("tester").build();
        memberRepository.save(member);
        profileRepository.save(profile);

        mockMvc.perform(post("/signup")
                        .with(TestUsers.unregistered(UUID.randomUUID()))
                        .with(csrf())
                        .param("nickname", "tester"))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"))
                .andExpect(model().attributeHasErrors("profileSetupRequest"));
    }

    @Test
    void signupForm_가입한사람_홈으로() throws Exception {
        Member member = Member.builder().role(MemberRole.USER).publicId(UUID.randomUUID()).build();
        Profile profile = Profile.builder().member(member).nickname("tester").build();
        memberRepository.save(member);
        profileRepository.save(profile);

        mockMvc.perform(get("/signup").with(TestUsers.registered(member, profile)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }
}
