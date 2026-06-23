package com.pigeonkim.paymentshop.board.service;

import com.pigeonkim.paymentshop.board.domain.Post;
import com.pigeonkim.paymentshop.board.domain.PostRepository;
import com.pigeonkim.paymentshop.board.domain.PostStatus;
import com.pigeonkim.paymentshop.board.dto.PostRequest;
import com.pigeonkim.paymentshop.member.domain.Member;
import com.pigeonkim.paymentshop.member.domain.MemberRepository;
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
    private MemberRepository memberRepository;

    @InjectMocks
    private PostService postService;

    private Member member(String email, long id, String nickname) {
        Member m = Member.builder()
                .email(email)
                .password("encoded")
                .name("테스트")
                .nickname(nickname)
                .role(MemberRole.USER)
                .build();
        ReflectionTestUtils.setField(m, "id", id);
        return m;
    }

    private PostRequest postRequest() {
        PostRequest r = new PostRequest();
        r.setTitle("제목 테스트");
        r.setContent("본문 테스트");
        r.setCommentsEnabled(true);
        return r;
    }

    @Test
    void createPost_성공() {
        Member member = member("test@test.com", 1L, "racoon");
        given(memberRepository.findByEmail(member.getEmail())).willReturn(Optional.of(member));

        postService.createPost(member.getEmail(), postRequest());

        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void createPost_회원없음_예외() {
        given(memberRepository.findByEmail("test@test.com")).willReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> postService.createPost("test@test.com", postRequest()));
    }

    @Test
    void updatePost_성공() {
        Member member = member("test@test.com", 1L, "racoon");

        Post post = Post.builder()
                .title("title123")
                .content("content123")
                .author(member)
                .commentsEnabled(false)
                .build();

        given(postRepository.findActiveById(1L, PostStatus.ACTIVE)).willReturn(Optional.of(post));
        given(memberRepository.findByEmail(member.getEmail())).willReturn(Optional.of(member));

        postService.updatePost(member.getEmail(), 1L, postRequest());

        assertEquals("제목 테스트", post.getTitle());
        assertEquals("본문 테스트", post.getContent());
        assertTrue(post.isCommentsEnabled());
    }

    @Test
    void updatePost_작성자아님_예외() {
        Member author = member("test@test.com", 1L, "racoon");
        Member other = member("test1@test.com", 2L, "racoon1");

        Post post = Post.builder()
                .title("title123")
                .content("content123")
                .author(author)
                .commentsEnabled(false)
                .build();

        given(postRepository.findActiveById(1L, PostStatus.ACTIVE)).willReturn(Optional.of(post));
        given(memberRepository.findByEmail(other.getEmail())).willReturn(Optional.of(other));

        assertThrows(IllegalArgumentException.class,
                () -> postService.updatePost(other.getEmail(), 1L, postRequest()));
    }

    @Test
    void deletePost_성공() {
        Member member = member("test@test.com", 1L, "racoon");

        Post post = Post.builder()
                .title("title123")
                .content("content123")
                .author(member)
                .commentsEnabled(false)
                .build();
        ReflectionTestUtils.setField(post, "id", 1L);

        given(postRepository.findActiveById(1L, PostStatus.ACTIVE)).willReturn(Optional.of(post));
        given(memberRepository.findByEmail(member.getEmail())).willReturn(Optional.of(member));

        postService.deletePost(member.getEmail(), post.getId());

        assertEquals(PostStatus.DELETED, post.getStatus());
    }

    @Test
    void deletePost_작성자아님_예외() {
        Member author = member("test@test.com", 1L, "racoon");
        Member other = member("test1@test.com", 2L, "racoon1");

        Post post = Post.builder()
                .title("title123")
                .content("content123")
                .author(author)
                .commentsEnabled(false)
                .build();
        ReflectionTestUtils.setField(post, "id", 1L);

        given(postRepository.findActiveById(1L, PostStatus.ACTIVE)).willReturn(Optional.of(post));
        given(memberRepository.findByEmail(other.getEmail())).willReturn(Optional.of(other));

        assertThrows(IllegalArgumentException.class,
                () -> postService.deletePost(other.getEmail(), 1L));
    }
}
