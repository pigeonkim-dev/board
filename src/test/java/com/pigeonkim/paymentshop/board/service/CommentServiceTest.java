package com.pigeonkim.paymentshop.board.service;

import com.pigeonkim.paymentshop.board.domain.*;
import com.pigeonkim.paymentshop.board.dto.CommentRequest;
import com.pigeonkim.paymentshop.member.domain.Member;
import com.pigeonkim.paymentshop.member.domain.MemberRole;
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
    private BoardProfileService boardProfileService;

    @InjectMocks
    private CommentService commentService;

    @Test
    void createComment_성공() {
        // given - 데이터 준비
        Member member = Member.builder()
                .email("test@test.com")
                .password("encoded")
                .name("테스트")
                .role(MemberRole.USER)
                .build();

        ReflectionTestUtils.setField(member, "id", 1L);

        BoardProfile author = BoardProfile.builder()
                .nickname("Raccoon")
                .member(member)
                .build();

        ReflectionTestUtils.setField(author, "id", 1L);

        Post post = Post.builder()
                .commentsEnabled(true)
                .author(author)
                .title("title")
                .content("content")
                .build();

        ReflectionTestUtils.setField(post, "id", 1L);

        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setContent("코멘트");

        given(boardProfileService.requireProfile(member.getEmail())).willReturn(author);
        given(postRepository.findActiveById(post.getId(), PostStatus.ACTIVE)).willReturn(Optional.of(post));

        commentService.createComment(member.getEmail(), post.getId(), commentRequest);

        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void createComment_댓글비허용_예외() {

        // given - 데이터 준비
        Member member = Member.builder()
                .email("test@test.com")
                .password("encoded")
                .name("테스트")
                .role(MemberRole.USER)
                .build();

        ReflectionTestUtils.setField(member, "id", 1L);

        BoardProfile author = BoardProfile.builder()
                .nickname("Raccoon")
                .member(member)
                .build();

        ReflectionTestUtils.setField(author, "id", 1L);

        Post post = Post.builder()
                .commentsEnabled(false)
                .author(author)
                .title("title")
                .content("content")
                .build();

        ReflectionTestUtils.setField(post, "id", 1L);

        given(postRepository.findActiveById(post.getId(), PostStatus.ACTIVE)).willReturn(Optional.of(post));
        //given(boardProfileService.requireProfile(member.getEmail())).willReturn(author);

        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setContent("코멘트");

        assertThrows(IllegalArgumentException.class, () -> commentService.createComment(member.getEmail(), post.getId(), commentRequest));
    }

    @Test
    void updateComment_성공() {

        // given - 데이터 준비
        Member member = Member.builder()
                .email("test@test.com")
                .password("encoded")
                .name("테스트")
                .role(MemberRole.USER)
                .build();

        ReflectionTestUtils.setField(member, "id", 1L);

        BoardProfile author = BoardProfile.builder()
                .nickname("Raccoon")
                .member(member)
                .build();

        ReflectionTestUtils.setField(author, "id", 1L);

        Post post = Post.builder()
                .commentsEnabled(false)
                .author(author)
                .title("title")
                .content("content")
                .build();

        ReflectionTestUtils.setField(post, "id", 1L);

        Comment comment = Comment.builder()
                .content("기존 내용")
                .post(post)
                .author(author).build();

        ReflectionTestUtils.setField(comment, "id", 1L);

        given(commentRepository.findById(comment.getId())).willReturn(Optional.of(comment));
        given(boardProfileService.requireProfile(member.getEmail())).willReturn(author);

        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setContent("새 내용");

        commentService.updateComment(member.getEmail(), post.getId(), comment.getId(), commentRequest);

        assertEquals("새 내용", comment.getContent());
    }

    @Test
    void updateComment_작성자아님_예외() {

        // given - 데이터 준비
        Member member = Member.builder()
                .email("test@test.com")
                .password("encoded")
                .name("테스트")
                .role(MemberRole.USER)
                .build();

        ReflectionTestUtils.setField(member, "id", 1L);

        BoardProfile author = BoardProfile.builder()
                .nickname("Raccoon")
                .member(member)
                .build();

        ReflectionTestUtils.setField(author, "id", 1L);

        Post post = Post.builder()
                .commentsEnabled(false)
                .author(author)
                .title("title")
                .content("content")
                .build();

        ReflectionTestUtils.setField(post, "id", 1L);

        Comment comment = Comment.builder()
                .content("기존 내용")
                .post(post)
                .author(author).build();

        ReflectionTestUtils.setField(comment, "id", 1L);

        // given - 데이터 준비
        Member member2 = Member.builder()
                .email("test@test.com")
                .password("encoded")
                .name("테스트")
                .role(MemberRole.USER)
                .build();

        ReflectionTestUtils.setField(member2, "id", 3L);

        BoardProfile author2 = BoardProfile.builder()
                .nickname("Raccoon")
                .member(member)
                .build();

        ReflectionTestUtils.setField(author2, "id", 3L);

        given(commentRepository.findById(comment.getId())).willReturn(Optional.of(comment));
        given(boardProfileService.requireProfile(member.getEmail())).willReturn(author2);

        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setContent("코멘트");

        assertThrows(IllegalArgumentException.class,
                () -> commentService.updateComment(member.getEmail(), post.getId(), comment.getId(), commentRequest));
    }

    @Test
    void deleteComment_성공() {

        // given - 데이터 준비
        Member member = Member.builder()
                .email("test@test.com")
                .password("encoded")
                .name("테스트")
                .role(MemberRole.USER)
                .build();

        ReflectionTestUtils.setField(member, "id", 1L);

        BoardProfile author = BoardProfile.builder()
                .nickname("Raccoon")
                .member(member)
                .build();

        ReflectionTestUtils.setField(author, "id", 1L);

        Post post = Post.builder()
                .commentsEnabled(false)
                .author(author)
                .title("title")
                .content("content")
                .build();

        ReflectionTestUtils.setField(post, "id", 1L);

        Comment comment = Comment.builder()
                .content("기존 내용")
                .post(post)
                .author(author).build();

        ReflectionTestUtils.setField(comment, "id", 1L);

        given(commentRepository.findById(comment.getId())).willReturn(Optional.of(comment));
        given(boardProfileService.requireProfile(member.getEmail())).willReturn(author);

        commentService.deleteComment(member.getEmail(), post.getId(), comment.getId());

        assertEquals(CommentStatus.DELETED, comment.getStatus());
    }
}
