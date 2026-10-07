package com.pigeonkim.board.service;

import com.pigeonkim.board.component.ProfileFinder;
import com.pigeonkim.board.domain.*;
import com.pigeonkim.board.domain.entity.*;
import com.pigeonkim.board.exception.BusinessException;
import com.pigeonkim.board.exception.ErrorCode;
import com.pigeonkim.board.repository.*;
import com.pigeonkim.board.service.result.CommentResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final ProfileFinder profileFinder;

    @Transactional(readOnly = true)
    public List<CommentResult> getComments(Long postId, UUID publicId) {

        List<Comment> commentList = commentRepository.findActiveCommentsByPostId(postId, CommentStatus.ACTIVE);

        Profile profile = publicId == null ? null : profileFinder.findByMemberPublicId(publicId);

        return commentList.stream().map((c) -> CommentResult.from(c, profile)).toList();
    }

    @Transactional
    public void createComment(UUID publicId, Long postId, String content) {

        // 1. 게시글 존재 확인
        Post post = postRepository.findActiveById(postId, PostStatus.ACTIVE)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        // 2. 댓글 허용 여부
        if (!post.isCommentsEnabled()) {
            throw new BusinessException(ErrorCode.COMMENTS_DISABLED);
        }

        Profile profile = profileFinder.findByMemberPublicId(publicId);

        Comment comment = Comment.builder()
                .post(post)
                .author(profile)
                .content(content)
                .build();

        commentRepository.save(comment);
    }

    @Transactional
    public void updateComment(UUID publicId, Long postId, Long commentId, String content) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));

        if (comment.getStatus() == CommentStatus.DELETED) {
            throw new BusinessException(ErrorCode.COMMENT_DELETED);
        }

        // postId 정합성 검증
        if (!comment.getPost().getId().equals(postId)) {
            throw new BusinessException(ErrorCode.POST_COMMENT_MISMATCH);
        }

        Profile profile = profileFinder.findByMemberPublicId(publicId);

        if (!comment.isAuthor(profile)) {
            throw new BusinessException(ErrorCode.NOT_COMMENT_AUTHOR);
        }

        // 삭제된 게시글 체크
        if (comment.getPost().getStatus() == PostStatus.DELETED) {
            throw new BusinessException(ErrorCode.POST_DELETED);
        }

        comment.update(content);
    }

    @Transactional
    public void deleteComment(UUID publicId, Long postId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));

        if (!comment.getPost().getId().equals(postId)) {
            throw new BusinessException(ErrorCode.POST_COMMENT_MISMATCH);
        }

        if (comment.getPost().getStatus() == PostStatus.DELETED) {
            throw new BusinessException(ErrorCode.POST_DELETED);
        }

        Profile profile = profileFinder.findByMemberPublicId(publicId);

        if (!comment.isAuthor(profile)) {
            throw new BusinessException(ErrorCode.NOT_COMMENT_AUTHOR);
        }

        comment.delete();
    }
}
