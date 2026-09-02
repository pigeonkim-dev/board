package com.pigeonkim.board.service;

import com.pigeonkim.board.component.MemberFinder;
import com.pigeonkim.board.domain.*;
import com.pigeonkim.board.domain.entity.*;
import com.pigeonkim.board.exception.ConflictStateException;
import com.pigeonkim.board.exception.ForbiddenException;
import com.pigeonkim.board.exception.NotFoundException;
import com.pigeonkim.board.repository.*;
import com.pigeonkim.board.web.dto.CommentRequest;
import com.pigeonkim.board.web.dto.CommentResponse;
import com.pigeonkim.board.domain.entity.Member;
import com.pigeonkim.board.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberFinder memberFinder;

    @Transactional(readOnly = true)
    public List<CommentResponse> getComments(Long postId, String email) {

        List<Comment> commentList = commentRepository.findActiveCommentsByPostId(postId, CommentStatus.ACTIVE);

        Member member = email == null ? null : memberFinder.getByEmail(email);

        return commentList.stream().map((c) -> CommentResponse.from(c, member)).toList();
    }

    @Transactional
    public void createComment(String email, Long postId, CommentRequest request) {

        // 1. 게시글 존재 확인
        Post post = postRepository.findActiveById(postId, PostStatus.ACTIVE)
                .orElseThrow(() -> new ConflictStateException("존재하지 않는 게시글입니다."));

        // 2. 댓글 허용 여부
        if (!post.isCommentsEnabled()) {
            throw new ConflictStateException("이 게시글은 댓글을 받지 않습니다.");
        }

        // 3. 회원 확인
        Member author = memberFinder.getByEmail(email);

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
                .orElseThrow(() -> new NotFoundException("존재하지 않는 댓글입니다."));

        if (comment.getStatus() == CommentStatus.DELETED) {
            throw new ConflictStateException("삭제된 댓글입니다.");
        }

        // postId 정합성 검증
        if (!comment.getPost().getId().equals(postId)) {
            throw new NotFoundException("게시글과 댓글이 일치하지 않습니다.");
        }

        Member author = memberFinder.getByEmail(email);

        if (!comment.isAuthor(author)) {
            throw new ForbiddenException("작성자가 아닙니다.");
        }

        // 삭제된 게시글 체크
        if (comment.getPost().getStatus() == PostStatus.DELETED) {
            throw new ConflictStateException("삭제된 게시글의 댓글은 수정할 수 없습니다.");
        }

        comment.update(request.getContent());
    }

    @Transactional
    public void deleteComment(String email, Long postId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 댓글입니다."));

        if (!comment.getPost().getId().equals(postId)) {
            throw new NotFoundException("게시글과 댓글이 일치하지 않습니다.");
        }

        if (comment.getPost().getStatus() == PostStatus.DELETED) {
            throw new ConflictStateException("삭제된 게시글의 댓글은 삭제할 수 없습니다.");
        }

        Member author = memberFinder.getByEmail(email);

        if (!comment.isAuthor(author)) {
            throw new ForbiddenException("작성자가 아닙니다.");
        }

        comment.delete();
    }
}
