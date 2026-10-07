package com.pigeonkim.board.web.controller;

import com.pigeonkim.board.domain.MemberRole;
import com.pigeonkim.board.domain.entity.Member;
import com.pigeonkim.board.domain.entity.Post;
import com.pigeonkim.board.domain.entity.Profile;
import com.pigeonkim.board.web.security.TestUsers;
import com.pigeonkim.board.repository.MemberRepository;
import com.pigeonkim.board.repository.PostRepository;
import com.pigeonkim.board.repository.ProfileRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** 회귀 둘. 9/18 에 실제로 터졌던 "남의 글 수정" 과, 비로그인 목록 조회. */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private PostRepository postRepository;

    @Test
    void postList_비로그인_200() throws Exception {
        mockMvc.perform(get("/board/posts"))
                .andExpect(status().isOk());

    }

    @Test
    void editForm_남의글_403() throws Exception {
        Member member1 = Member.builder().role(MemberRole.USER).publicId(UUID.randomUUID()).build();
        memberRepository.save(member1);
        Profile profile1 = Profile.builder().member(member1).nickname("test1").build();
        profileRepository.save(profile1);

        Post post = Post.builder().author(profile1).title("test1").content("test2").commentsEnabled(false).build();
        postRepository.save(post);

        Member member2 = Member.builder().role(MemberRole.USER).publicId(UUID.randomUUID()).build();
        memberRepository.save(member2);
        Profile profile2 = Profile.builder().member(member2).nickname("test2").build();
        profileRepository.save(profile2);

        mockMvc.perform(get("/board/posts/{id}/edit", post.getId())
                .with(TestUsers.registered(member2, profile2)))
                .andExpect(status().isForbidden());

    }
}
