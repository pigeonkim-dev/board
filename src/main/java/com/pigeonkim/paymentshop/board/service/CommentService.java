package com.pigeonkim.paymentshop.board.service;

import com.pigeonkim.paymentshop.board.domain.*;
import com.pigeonkim.paymentshop.board.dto.CommentRequest;
import com.pigeonkim.paymentshop.board.dto.CommentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final BoardProfileService boardProfileService;

    @Transactional(readOnly = true)
    public List<CommentResponse> getComments(Long postId) {

        List<Comment> commnetList = commentRepository.findActiveCommentsByPostId(postId, CommentStatus.ACTIVE);

        return commnetList.stream().map(CommentResponse::from).toList();
    }

    @Transactional
    public Long createComment(String email, Long postId, CommentRequest request) {

        BoardProfile boardProfile = boardProfileService.requireProfile(email);

        Post post = postRepository.findActiveById(postId, PostStatus.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("존재 하지 않는 게시글 입니다."));

        if (!post.isCommentsEnabled()) {
            throw new IllegalArgumentException("이 게시글은 댓글을 받지 않습니다.");
        }

        Comment comment = Comment.builder()
                .post(post)
                .author(boardProfile)
                .content(request.getContent())
                .build();

        commentRepository.save(comment);

        return comment.getId();
    }

    @Transactional
    public void updateComment(String email, Long commentId, CommentRequest request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재 하지 않는 댓글 입니다."));

        if (comment.getStatus() == CommentStatus.DELETED){
            throw new IllegalArgumentException("삭제 된 댓글 입니다.");
        }

        BoardProfile profile= boardProfileService.requireProfile(email);

        if (!comment.isAuthor(profile)){
            throw new IllegalArgumentException("작성자가 아닙니다.");
        }

        comment.update(request.getContent());

    }

    @Transactional
    public void deleteComment(String email, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재 하지 않는 댓글 입니다."));

        if (comment.getStatus() == CommentStatus.DELETED){
            throw new IllegalArgumentException("이미 삭제 된 댓글 입니다.");
        }

        BoardProfile profile= boardProfileService.requireProfile(email);

        if (!comment.isAuthor(profile)){
            throw new IllegalArgumentException("작성자가 아닙니다.");
        }

        comment.delete();
    }
}
