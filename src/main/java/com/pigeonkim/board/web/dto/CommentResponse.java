package com.pigeonkim.board.web.dto;

import com.pigeonkim.board.domain.entity.Member;
import com.pigeonkim.board.domain.entity.Comment;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentResponse {
    private Long id;
    private String content;
    private String authorNickname;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean canDelete;
    private boolean canEdit;

    private CommentResponse(
            Long id, String content, String nickname, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.content = content;
        this.authorNickname = nickname;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static CommentResponse from(Comment comment, Member member) {
        CommentResponse commentResponse = new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getAuthor().getNickname(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );

        if (comment.isAuthor(member)) {
            commentResponse.canDelete = true;
            commentResponse.canEdit = true;
        }

        return commentResponse;
    }
}
