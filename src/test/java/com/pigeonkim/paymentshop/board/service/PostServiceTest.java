package com.pigeonkim.paymentshop.board.service;

import com.pigeonkim.paymentshop.board.domain.BoardProfile;
import com.pigeonkim.paymentshop.board.domain.Post;
import com.pigeonkim.paymentshop.board.domain.PostRepository;
import com.pigeonkim.paymentshop.board.domain.PostStatus;
import com.pigeonkim.paymentshop.board.dto.PostRequest;
import com.pigeonkim.paymentshop.board.exception.NicknameRequiredException;
import com.pigeonkim.paymentshop.member.domain.Member;
import com.pigeonkim.paymentshop.member.domain.MemberRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private BoardProfileService boardProfileService;

    @InjectMocks
    private PostService postService;

    @Test
    void createPost_성공() {

        Member member = Member.builder()
                .email("test@test.com")
                .password("encoded")
                .name("테스트")
                .role(MemberRole.USER)
                .build();

        BoardProfile boardProfile = BoardProfile.builder()
                .member(member)
                .nickname("racoon")
                .build();

        PostRequest postRequest = new PostRequest();
        postRequest.setTitle("제목 테스트");
        postRequest.setContent("본문 테스트");
        postRequest.setCommentsEnabled(true);

        given(boardProfileService.requireProfile(member.getEmail())).willReturn(boardProfile);

        postService.createPost(member.getEmail(), postRequest);

        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void createPost_프로필없음_예외() {

        Member member = Member.builder()
                .email("test@test.com")
                .password("encoded")
                .name("테스트")
                .role(MemberRole.USER)
                .build();

        PostRequest postRequest = new PostRequest();
        postRequest.setTitle("제목 테스트");
        postRequest.setContent("본문 테스트");
        postRequest.setCommentsEnabled(true);

        given(boardProfileService.requireProfile(member.getEmail())).willThrow(new NicknameRequiredException("닉네임 설정이 필요합니다."));

        assertThrows(NicknameRequiredException.class, () -> postService.createPost(member.getEmail(), postRequest));
    }

    @Test
    void updatePost_성공() {

        Member member = Member.builder()
                .email("test@test.com")
                .password("encoded")
                .name("테스트")
                .role(MemberRole.USER)
                .build();

        ReflectionTestUtils.setField(member, "id", 1L);

        PostRequest postRequest = new PostRequest();
        postRequest.setTitle("제목 테스트");
        postRequest.setContent("본문 테스트");
        postRequest.setCommentsEnabled(true);

        BoardProfile boardProfile = BoardProfile.builder()
                .member(member)
                .nickname("racoon")
                .build();
        ReflectionTestUtils.setField(boardProfile, "id", 1L);

        Post post = Post.builder()
                .title("title123")
                .content("content123")
                .author(boardProfile)
                .commentsEnabled(false)
                .build();

        given(postRepository.findActiveById(1L, PostStatus.ACTIVE)).willReturn(Optional.of(post));
        given(boardProfileService.requireProfile(member.getEmail())).willReturn(boardProfile);

        postService.updatePost(member.getEmail(), 1L, postRequest);

        assertEquals("제목 테스트", post.getTitle());
        assertEquals("본문 테스트", post.getContent());
        assertTrue(post.isCommentsEnabled());
    }

    @Test
    void updatePost_작성자아님_예외() {

        Member member = Member.builder()
                .email("test@test.com")
                .password("encoded")
                .name("테스트")
                .role(MemberRole.USER)
                .build();
        ReflectionTestUtils.setField(member, "id", 1L);

        BoardProfile boardProfile = BoardProfile.builder()
                .member(member)
                .nickname("racoon")
                .build();
        ReflectionTestUtils.setField(boardProfile, "id", 1L);

        Post post = Post.builder()
                .title("title123")
                .content("content123")
                .author(boardProfile)
                .commentsEnabled(false)
                .build();

        PostRequest postRequest = new PostRequest();
        postRequest.setTitle("제목 테스트");
        postRequest.setContent("본문 테스트");
        postRequest.setCommentsEnabled(true);

        Member memberErr = Member.builder()
                .email("test1@test.com")
                .password("encoded")
                .name("테스트1")
                .role(MemberRole.USER)
                .build();

        ReflectionTestUtils.setField(memberErr, "id", 2L);

        BoardProfile boardProfileErr = BoardProfile.builder()
                .member(memberErr)
                .nickname("racoon1")
                .build();

        ReflectionTestUtils.setField(boardProfileErr, "id", 2L);

        given(postRepository.findActiveById(1L, PostStatus.ACTIVE)).willReturn(Optional.of(post));
        //다른 프로필 반환
        given(boardProfileService.requireProfile(member.getEmail())).willReturn(boardProfileErr);

        assertThrows(IllegalArgumentException.class, () -> postService.updatePost(member.getEmail(), 1L, postRequest));
    }

    @Test
    void deletePost_성공() {

        Member member = Member.builder()
                .email("test@test.com")
                .password("encoded")
                .name("테스트")
                .role(MemberRole.USER)
                .build();

        ReflectionTestUtils.setField(member, "id", 1L);

        BoardProfile boardProfile = BoardProfile.builder()
                .member(member)
                .nickname("racoon")
                .build();
        ReflectionTestUtils.setField(boardProfile, "id", 1L);

        Post post = Post.builder()
                .title("title123")
                .content("content123")
                .author(boardProfile)
                .commentsEnabled(false)
                .build();

        ReflectionTestUtils.setField(post, "id", 1L);

        given(postRepository.findActiveById(1L, PostStatus.ACTIVE)).willReturn(Optional.of(post));
        given(boardProfileService.requireProfile(member.getEmail())).willReturn(boardProfile);

        postService.deletePost(member.getEmail(), post.getId());

        assertEquals(PostStatus.DELETED, post.getStatus());
    }

    @Test
    void deletePost_작성자아님_예외() {

        Member member = Member.builder()
                .email("test@test.com")
                .password("encoded")
                .name("테스트")
                .role(MemberRole.USER)
                .build();
        ReflectionTestUtils.setField(member, "id", 1L);

        BoardProfile boardProfile = BoardProfile.builder()
                .member(member)
                .nickname("racoon")
                .build();
        ReflectionTestUtils.setField(boardProfile, "id", 1L);

        Post post = Post.builder()
                .title("title123")
                .content("content123")
                .author(boardProfile)
                .commentsEnabled(false)
                .build();

        ReflectionTestUtils.setField(post, "id", 1L);

        PostRequest postRequest = new PostRequest();
        postRequest.setTitle("제목 테스트");
        postRequest.setContent("본문 테스트");
        postRequest.setCommentsEnabled(true);

        Member memberErr = Member.builder()
                .email("test1@test.com")
                .password("encoded")
                .name("테스트1")
                .role(MemberRole.USER)
                .build();

        ReflectionTestUtils.setField(memberErr, "id", 2L);

        BoardProfile boardProfileErr = BoardProfile.builder()
                .member(memberErr)
                .nickname("racoon1")
                .build();

        ReflectionTestUtils.setField(boardProfileErr, "id", 2L);

        given(postRepository.findActiveById(1L, PostStatus.ACTIVE)).willReturn(Optional.of(post));
        //다른 프로필 반환
        given(boardProfileService.requireProfile(member.getEmail())).willReturn(boardProfileErr);

        assertThrows(IllegalArgumentException.class, () -> postService.deletePost(member.getEmail(), 1L));
    }
}