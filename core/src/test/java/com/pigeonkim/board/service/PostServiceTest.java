package com.pigeonkim.board.service;

import com.pigeonkim.board.component.ProfileFinder;
import com.pigeonkim.board.domain.entity.Profile;
import com.pigeonkim.board.exception.BusinessException;
import com.pigeonkim.board.exception.ErrorCode;
import com.pigeonkim.board.domain.entity.Post;
import com.pigeonkim.board.repository.PostRepository;
import com.pigeonkim.board.domain.PostStatus;
import com.pigeonkim.board.service.command.PostCommand;
import com.pigeonkim.board.domain.entity.Member;
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
    private ProfileFinder profileFinder;

    @InjectMocks
    private PostService postService;

    private Member member(String email, long id, String nickname) {
        Member m = Member.builder()
                .email(email)
                .password("encoded")
                .role(MemberRole.USER)
                .build();
        ReflectionTestUtils.setField(m, "id", id);
        return m;
    }

    private PostCommand postRequest() {
        PostCommand postCommand = PostCommand.of("제목 테스트", "본문 테스트", true);
        return postCommand;
    }

    @Test
    void createPost_성공() {
        Member member = member("test@test.com", 1L, "racoon");

        Profile profile = new Profile(member, "test");
        ReflectionTestUtils.setField(profile, "id", 1L);

        given(profileFinder.findByMemberEmail(member.getEmail())).willReturn(profile);

        postService.createPost(member.getEmail(), postRequest());

        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void createPost_회원없음_예외() {

        given(profileFinder.findByMemberEmail("test@test.com")).willThrow(new BusinessException(ErrorCode.PROFILE_NOT_FOUND));

        assertThrows(BusinessException.class,
                () -> postService.createPost("test@test.com", postRequest()));
    }

    @Test
    void updatePost_성공() {
        Member member = member("test@test.com", 1L, "racoon");

        Profile profile = new Profile(member, "test");
        ReflectionTestUtils.setField(profile, "id", 1L);

        Post post = Post.builder()
                .title("title123")
                .content("content123")
                .author(profile)
                .commentsEnabled(false)
                .build();

        given(postRepository.findActiveById(1L, PostStatus.ACTIVE)).willReturn(Optional.of(post));
        given(profileFinder.findByMemberEmail(member.getEmail())).willReturn(profile);

        postService.updatePost(member.getEmail(), 1L, postRequest());

        assertEquals("제목 테스트", post.getTitle());
        assertEquals("본문 테스트", post.getContent());
        assertTrue(post.isCommentsEnabled());
    }

    @Test
    void updatePost_작성자아님_예외() {
        Member author = member("test@test.com", 1L, "racoon");
        Member other = member("test1@test.com", 2L, "racoon1");

        Profile profile = new Profile(author, "test");
        ReflectionTestUtils.setField(profile, "id", 1L);

        Profile profile2 = new Profile(other, "test2");
        ReflectionTestUtils.setField(profile2, "id", 2L);

        Post post = Post.builder()
                .title("title123")
                .content("content123")
                .author(profile)
                .commentsEnabled(false)
                .build();

        given(postRepository.findActiveById(1L, PostStatus.ACTIVE)).willReturn(Optional.of(post));
        given(profileFinder.findByMemberEmail(other.getEmail())).willReturn(profile2);

        BusinessException e = assertThrows(BusinessException.class,
                () -> postService.updatePost(other.getEmail(), 1L, postRequest()));

        assertEquals(ErrorCode.NOT_POST_AUTHOR, e.getErrorCode());
    }

    @Test
    void deletePost_성공() {
        Member member = member("test@test.com", 1L, "racoon");

        Profile profile = new Profile(member, "test");
        ReflectionTestUtils.setField(profile, "id", 1L);

        Post post = Post.builder()
                .title("title123")
                .content("content123")
                .author(profile)
                .commentsEnabled(false)
                .build();
        ReflectionTestUtils.setField(post, "id", 1L);

        given(postRepository.findActiveById(1L, PostStatus.ACTIVE)).willReturn(Optional.of(post));
        given(profileFinder.findByMemberEmail(member.getEmail())).willReturn(profile);

        postService.deletePost(member.getEmail(), post.getId());

        assertEquals(PostStatus.DELETED, post.getStatus());
    }

    @Test
    void deletePost_작성자아님_예외() {
        Member author = member("test@test.com", 1L, "racoon");
        Member other = member("test1@test.com", 2L, "racoon1");

        Profile profile = new Profile(author, "test");
        ReflectionTestUtils.setField(profile, "id", 1L);

        Profile profile2 = new Profile(other, "test2");
        ReflectionTestUtils.setField(profile2, "id", 2L);

        Post post = Post.builder()
                .title("title123")
                .content("content123")
                .author(profile)
                .commentsEnabled(false)
                .build();
        ReflectionTestUtils.setField(post, "id", 1L);

        given(postRepository.findActiveById(1L, PostStatus.ACTIVE)).willReturn(Optional.of(post));
        given(profileFinder.findByMemberEmail(other.getEmail())).willReturn(profile2);

        BusinessException e = assertThrows(BusinessException.class,
                () -> postService.deletePost(other.getEmail(), 1L));

        assertEquals(ErrorCode.NOT_POST_AUTHOR, e.getErrorCode());
    }
}
