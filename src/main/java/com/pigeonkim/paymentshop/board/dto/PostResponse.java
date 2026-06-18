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
    private long commentCount;       // ← 추가
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PostResponse from(Post post, long commentCount) {
        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getAuthor().getNickname(),
                post.isCommentsEnabled(),
                commentCount,            // ← 추가
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}
