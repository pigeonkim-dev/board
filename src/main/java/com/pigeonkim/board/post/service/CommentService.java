package com.pigeonkim.board.post.service;

import com.pigeonkim.board.post.domain.*;
import com.pigeonkim.board.post.dto.CommentRequest;
import com.pigeonkim.board.post.dto.CommentResponse;
import com.pigeonkim.board.member.domain.Member;
import com.pigeonkim.board.member.domain.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    private Member getMember(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getComments(Long postId) {

        List<Comment> commnetList = commentRepository.findActiveCommentsByPostId(postId, CommentStatus.ACTIVE);

        return commnetList.stream().map(CommentResponse::from).toList();
    }

    @Transactional
    public void createComment(String email, Long postId, CommentRequest request) {

        // 1. 게시글 존재 확인
        Post post = postRepository.findActiveById(postId, PostStatus.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        // 2. 댓글 허용 여부
        if (!post.isCommentsEnabled()) {
            throw new IllegalArgumentException("이 게시글은 댓글을 받지 않습니다.");
        }

        // 3. 회원 확인
        Member author = getMember(email);

        Comment comment = Comment.builder()
                .post(post)
                .author(author)
                .content(request.getContent())
                .build();

        commentRepository.save(comment);
    }

    @Transactional
    public void updateComment(String email, Long postId, Long commentId, CommentRequest request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));

        if (comment.getStatus() == CommentStatus.DELETED) {
            throw new IllegalArgumentException("삭제된 댓글입니다.");
        }

        // postId 정합성 검증
        if (!comment.getPost().getId().equals(postId)) {
            throw new IllegalArgumentException("게시글과 댓글이 일치하지 않습니다.");
        }

        Member author = getMember(email);

        if (!comment.isAuthor(author)) {
            throw new IllegalArgumentException("작성자가 아닙니다.");
        }

        // 삭제된 게시글 체크
        if (comment.getPost().getStatus() == PostStatus.DELETED) {
            throw new IllegalArgumentException("삭제된 게시글의 댓글은 수정할 수 없습니다.");
        }

        comment.update(request.getContent());
    }

    @Transactional
    public void deleteComment(String email, Long postId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));

        if (!comment.getPost().getId().equals(postId)) {
            throw new IllegalArgumentException("게시글과 댓글이 일치하지 않습니다.");
        }

        if (comment.getPost().getStatus() == PostStatus.DELETED) {
            throw new IllegalArgumentException("삭제된 게시글의 댓글은 수정할 수 없습니다.");
        }

        Member author = getMember(email);

        if (!comment.isAuthor(author)) {
            throw new IllegalArgumentException("작성자가 아닙니다.");
        }

        comment.delete();
    }
}
