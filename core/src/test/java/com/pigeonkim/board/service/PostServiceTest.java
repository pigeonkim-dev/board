package com.pigeonkim.board.service;

import com.pigeonkim.board.component.ProfileFinder;
import com.pigeonkim.board.domain.MemberRole;
import com.pigeonkim.board.domain.PostStatus;
import com.pigeonkim.board.domain.entity.Member;
import com.pigeonkim.board.domain.entity.Post;
import com.pigeonkim.board.domain.entity.Profile;
import com.pigeonkim.board.exception.BusinessException;
import com.pigeonkim.board.exception.ErrorCode;
import com.pigeonkim.board.repository.PostRepository;
import com.pigeonkim.board.service.command.PostCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

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

    private Member member(long id, String nickname) {
        Member m = Member.builder()
                .role(MemberRole.USER)
                .publicId(UUID.randomUUID())
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
        Member member = member(1L, "racoon");

        Profile profile = new Profile(member, "test");
        ReflectionTestUtils.setField(profile, "id", 1L);

        given(profileFinder.findByMemberPublicId(member.getPublicId())).willReturn(profile);

        postService.createPost(member.getPublicId(), postRequest());

        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void createPost_회원없음_예외() {

        given(profileFinder.findByMemberPublicId(
                UUID.fromString("11111111-1111-1111-1111-111111111111")))
                .willThrow(new BusinessException(ErrorCode.PROFILE_NOT_FOUND));

        assertThrows(BusinessException.class,
                () -> postService.createPost(UUID.fromString("11111111-1111-1111-1111-111111111111"), postRequest()));
    }

    @Test
    void updatePost_성공() {
        Member member = member(1L, "racoon");

        Profile profile = new Profile(member, "test");
        ReflectionTestUtils.setField(profile, "id", 1L);

        Post post = Post.builder()
                .title("title123")
                .content("content123")
                .author(profile)
                .commentsEnabled(false)
                .build();

        given(postRepository.findActiveById(1L, PostStatus.ACTIVE)).willReturn(Optional.of(post));
        given(profileFinder.findByMemberPublicId(member.getPublicId())).willReturn(profile);

        postService.updatePost(member.getPublicId(), 1L, postRequest());

        assertEquals("제목 테스트", post.getTitle());
        assertEquals("본문 테스트", post.getContent());
        assertTrue(post.isCommentsEnabled());
    }

    @Test
    void updatePost_작성자아님_예외() {
        Member author = member(1L, "racoon");
        Member other = member(2L, "racoon1");


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
        given(profileFinder.findByMemberPublicId(other.getPublicId())).willReturn(profile2);

        BusinessException e = assertThrows(BusinessException.class,
                () -> postService.updatePost(other.getPublicId(), 1L, postRequest()));

        assertEquals(ErrorCode.NOT_POST_AUTHOR, e.getErrorCode());
    }

    @Test
    void deletePost_성공() {
        Member member = member(1L, "racoon");

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
        given(profileFinder.findByMemberPublicId(member.getPublicId())).willReturn(profile);

        postService.deletePost(member.getPublicId(), post.getId());

        assertEquals(PostStatus.DELETED, post.getStatus());
    }

    @Test
    void deletePost_작성자아님_예외() {
        Member author = member(1L, "racoon");
        Member other = member(2L, "racoon1");

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
        given(profileFinder.findByMemberPublicId(other.getPublicId())).willReturn(profile2);

        BusinessException e = assertThrows(BusinessException.class,
                () -> postService.deletePost(other.getPublicId(), 1L));

        assertEquals(ErrorCode.NOT_POST_AUTHOR, e.getErrorCode());
    }
}
