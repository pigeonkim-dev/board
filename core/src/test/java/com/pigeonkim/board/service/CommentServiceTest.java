package com.pigeonkim.board.service;

import com.pigeonkim.board.component.ProfileFinder;
import com.pigeonkim.board.domain.*;
import com.pigeonkim.board.domain.entity.*;
import com.pigeonkim.board.exception.ConflictStateException;
import com.pigeonkim.board.exception.ForbiddenException;
import com.pigeonkim.board.repository.*;
import com.pigeonkim.board.domain.entity.Member;
import com.pigeonkim.board.domain.MemberRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {
    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private ProfileFinder profileFinder;

    @InjectMocks
    private CommentService commentService;

    private Member member(String email, long id, String nickname) {
        Member m = Member.builder()
                .email(email)
                .password("encoded")
                .role(MemberRole.USER)
                .build();
        ReflectionTestUtils.setField(m, "id", id);

        return m;
    }

    private Post post(Profile profile, boolean commentsEnabled) {
        Post post = Post.builder()
                .commentsEnabled(commentsEnabled)
                .author(profile)
                .title("title")
                .content("content")
                .build();
        ReflectionTestUtils.setField(post, "id", 1L);

        return post;
    }

    @Test
    void createComment_성공() {
        Member member = member("test@test.com", 1L, "Raccoon");

        Profile profile = new Profile(member, "test");
        ReflectionTestUtils.setField(profile, "id", 1L);

        Post post = post(profile, true);

        given(profileFinder.findByMemberEmail(member.getEmail())).willReturn(profile);
        given(postRepository.findActiveById(post.getId(), PostStatus.ACTIVE)).willReturn(Optional.of(post));

        commentService.createComment(member.getEmail(), post.getId(), "코멘트");

        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void createComment_댓글비허용_예외() {
        Member member = member("test@test.com", 1L, "Raccoon");

        Profile profile = new Profile(member, "test");
        ReflectionTestUtils.setField(profile, "id", 1L);

        Post post = post(profile, false);

        given(postRepository.findActiveById(post.getId(), PostStatus.ACTIVE)).willReturn(Optional.of(post));

        assertThrows(ConflictStateException.class,
                () -> commentService.createComment(member.getEmail(), post.getId(), "코멘트"));
    }

    @Test
    void updateComment_성공() {
        Member member = member("test@test.com", 1L, "Raccoon");

        Profile profile = new Profile(member, "test");
        ReflectionTestUtils.setField(profile, "id", 1L);

        Post post = post(profile, false);

        Comment comment = Comment.builder()
                .content("기존 내용")
                .post(post)
                .author(profile)
                .build();
        ReflectionTestUtils.setField(comment, "id", 1L);

        given(commentRepository.findById(comment.getId())).willReturn(Optional.of(comment));
        given(profileFinder.findByMemberEmail(member.getEmail())).willReturn(profile);

        commentService.updateComment(member.getEmail(), post.getId(), comment.getId(), "새 내용");

        assertEquals("새 내용", comment.getContent());
    }

    @Test
    void updateComment_작성자아님_예외() {
        Member author = member("test@test.com", 1L, "Raccoon");
        Member other = member("other@test.com", 3L, "Fox");

        Profile profile = new Profile(author, "test");
        ReflectionTestUtils.setField(profile, "id", 1L);

        Profile profile2 = new Profile(other, "test2");
        ReflectionTestUtils.setField(profile2, "id", 2L);

        Post post = post(profile, false);

        Comment comment = Comment.builder()
                .content("기존 내용")
                .post(post)
                .author(profile)
                .build();

        ReflectionTestUtils.setField(comment, "id", 1L);

        given(commentRepository.findById(comment.getId())).willReturn(Optional.of(comment));
        given(profileFinder.findByMemberEmail(other.getEmail())).willReturn(profile2);

        assertThrows(ForbiddenException.class,
                () -> commentService.updateComment(other.getEmail(), post.getId(), comment.getId(), "코멘트"));
    }

    @Test
    void deleteComment_성공() {
        Member member = member("test@test.com", 1L, "Raccoon");

        Profile profile = new Profile(member, "test");
        ReflectionTestUtils.setField(profile, "id", 1L);

        Post post = post(profile, false);

        Comment comment = Comment.builder()
                .content("기존 내용")
                .post(post)
                .author(profile)
                .build();
        ReflectionTestUtils.setField(comment, "id", 1L);

        given(commentRepository.findById(comment.getId())).willReturn(Optional.of(comment));
        given(profileFinder.findByMemberEmail(member.getEmail())).willReturn(profile);

        commentService.deleteComment(member.getEmail(), post.getId(), comment.getId());

        assertEquals(CommentStatus.DELETED, comment.getStatus());
    }
}
