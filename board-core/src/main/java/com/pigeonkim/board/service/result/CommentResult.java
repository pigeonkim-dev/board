package com.pigeonkim.board.service.result;

import com.pigeonkim.board.domain.entity.Comment;
import com.pigeonkim.board.domain.entity.Profile;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentResult {
    private Long id;
    private String content;
    private String authorNickname;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean canDelete;
    private boolean canEdit;

    private CommentResult(
            Long id, String content, String nickname, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.content = content;
        this.authorNickname = nickname;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static CommentResult from(Comment comment, Profile profile) {
        CommentResult commentResponse = new CommentResult(
                comment.getId(),
                comment.getContent(),
                comment.getAuthor().getNickname(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );

        if (comment.isAuthor(profile)) {
            commentResponse.canDelete = true;
            commentResponse.canEdit = true;
        }

        return commentResponse;
    }
}
