package com.pigeonkim.board.post.service;

import com.pigeonkim.board.post.domain.*;
import com.pigeonkim.board.post.dto.CommentRequest;
import com.pigeonkim.board.member.domain.Member;
import com.pigeonkim.board.member.domain.MemberRepository;
import com.pigeonkim.board.member.domain.MemberRole;
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
    private MemberRepository memberRepository;

    @InjectMocks
    private CommentService commentService;

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

    private Post post(Member author, boolean commentsEnabled) {
        Post post = Post.builder()
                .commentsEnabled(commentsEnabled)
                .author(author)
                .title("title")
                .content("content")
                .build();
        ReflectionTestUtils.setField(post, "id", 1L);
        return post;
    }

    @Test
    void createComment_성공() {
        Member member = member("test@test.com", 1L, "Raccoon");
        Post post = post(member, true);

        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setContent("코멘트");

        given(memberRepository.findByEmail(member.getEmail())).willReturn(Optional.of(member));
        given(postRepository.findActiveById(post.getId(), PostStatus.ACTIVE)).willReturn(Optional.of(post));

        commentService.createComment(member.getEmail(), post.getId(), commentRequest);

        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void createComment_댓글비허용_예외() {
        Member member = member("test@test.com", 1L, "Raccoon");
        Post post = post(member, false);

        given(postRepository.findActiveById(post.getId(), PostStatus.ACTIVE)).willReturn(Optional.of(post));

        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setContent("코멘트");

        assertThrows(IllegalArgumentException.class,
                () -> commentService.createComment(member.getEmail(), post.getId(), commentRequest));
    }

    @Test
    void updateComment_성공() {
        Member member = member("test@test.com", 1L, "Raccoon");
        Post post = post(member, false);

        Comment comment = Comment.builder()
                .content("기존 내용")
                .post(post)
                .author(member)
                .build();
        ReflectionTestUtils.setField(comment, "id", 1L);

        given(commentRepository.findById(comment.getId())).willReturn(Optional.of(comment));
        given(memberRepository.findByEmail(member.getEmail())).willReturn(Optional.of(member));

        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setContent("새 내용");

        commentService.updateComment(member.getEmail(), post.getId(), comment.getId(), commentRequest);

        assertEquals("새 내용", comment.getContent());
    }

    @Test
    void updateComment_작성자아님_예외() {
        Member author = member("test@test.com", 1L, "Raccoon");
        Member other = member("other@test.com", 3L, "Fox");
        Post post = post(author, false);

        Comment comment = Comment.builder()
                .content("기존 내용")
                .post(post)
                .author(author)
                .build();
        ReflectionTestUtils.setField(comment, "id", 1L);

        given(commentRepository.findById(comment.getId())).willReturn(Optional.of(comment));
        given(memberRepository.findByEmail(other.getEmail())).willReturn(Optional.of(other));

        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setContent("코멘트");

        assertThrows(IllegalArgumentException.class,
                () -> commentService.updateComment(other.getEmail(), post.getId(), comment.getId(), commentRequest));
    }

    @Test
    void deleteComment_성공() {
        Member member = member("test@test.com", 1L, "Raccoon");
        Post post = post(member, false);

        Comment comment = Comment.builder()
                .content("기존 내용")
                .post(post)
                .author(member)
                .build();
        ReflectionTestUtils.setField(comment, "id", 1L);

        given(commentRepository.findById(comment.getId())).willReturn(Optional.of(comment));
        given(memberRepository.findByEmail(member.getEmail())).willReturn(Optional.of(member));

        commentService.deleteComment(member.getEmail(), post.getId(), comment.getId());

        assertEquals(CommentStatus.DELETED, comment.getStatus());
    }
}
