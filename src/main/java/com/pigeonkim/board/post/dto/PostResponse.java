package com.pigeonkim.board.post.dto;

import com.pigeonkim.board.member.domain.Member;
import com.pigeonkim.board.post.domain.Post;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostResponse {
    private Long id;
    private String title;
    private String content;
    private String authorNickname;
    private boolean commentsEnabled;
    private long commentCount;       // ← 추가
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private  boolean canEdit;
    private boolean canDelete;

    private PostResponse(
            Long id, String title, String content, String nickname, boolean commentsEnabled,
            long commentCount, LocalDateTime createdAt, LocalDateTime updatedAt) {

        this.id = id;
        this.title = title;
        this.content = content;
        this.authorNickname = nickname;
        this.commentsEnabled = commentsEnabled;
        this.commentCount = commentCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static PostResponse from(Post post, long commentCount, Member member) {

        PostResponse postResponse = new PostResponse(post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getAuthor().getNickname(),
                post.isCommentsEnabled(),
                commentCount,            // ← 추가
                post.getCreatedAt(),
                post.getUpdatedAt());

        if (post.isAuthor(member)) {
            postResponse.canDelete = true;
            postResponse.canEdit = true;
        }

        return postResponse;
    }
}
