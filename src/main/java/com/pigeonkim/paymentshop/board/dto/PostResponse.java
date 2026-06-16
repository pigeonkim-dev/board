package com.pigeonkim.paymentshop.board.dto;

import com.pigeonkim.paymentshop.board.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PostResponse {
    private Long id;
    private String title;
    private String content;
    private String authorNickname;
    private boolean commentsEnabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PostResponse from(Post post) {
        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getAuthor().getNickname(),
                post.isCommentsEnabled(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}
