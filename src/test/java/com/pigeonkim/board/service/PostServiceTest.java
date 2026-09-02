package com.pigeonkim.board.service;

import com.pigeonkim.board.component.MemberFinder;
import com.pigeonkim.board.exception.ForbiddenException;
import com.pigeonkim.board.exception.NotFoundException;
import com.pigeonkim.board.domain.entity.Post;
import com.pigeonkim.board.repository.PostRepository;
import com.pigeonkim.board.domain.PostStatus;
import com.pigeonkim.board.web.dto.PostRequest;
import com.pigeonkim.board.domain.entity.Member;
import com.pigeonkim.board.repository.MemberRepository;
import com.pigeonkim.board.domain.MemberRole;
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
    private MemberFinder memberFinder;

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
        given(memberFinder.getByEmail(member.getEmail())).willReturn(member);

        postService.createPost(member.getEmail(), postRequest());

        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void createPost_회원없음_예외() {

        given(memberFinder.getByEmail("test@test.com")).willThrow(new NotFoundException("not found"));

        assertThrows(NotFoundException.class,
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
        given(memberFinder.getByEmail(member.getEmail())).willReturn(member);

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
        given(memberFinder.getByEmail(other.getEmail())).willReturn(other);

        assertThrows(ForbiddenException.class,
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
        given(memberFinder.getByEmail(member.getEmail())).willReturn(member);

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
        given(memberFinder.getByEmail(other.getEmail())).willReturn(other);

        assertThrows(ForbiddenException.class,
                () -> postService.deletePost(other.getEmail(), 1L));
    }
}
