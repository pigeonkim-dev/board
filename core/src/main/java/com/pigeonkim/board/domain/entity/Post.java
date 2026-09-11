package com.pigeonkim.board.domain.entity;

import com.pigeonkim.board.domain.PostStatus;

import com.pigeonkim.board.domain.entity.BaseEntity;
import com.pigeonkim.board.domain.entity.Member;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "posts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseEntity implements Authored {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Profile author;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private boolean commentsEnabled;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostStatus status;

    @Builder
    public Post(Profile author, String title, String content, boolean commentsEnabled) {
        this.author = author;
        this.title = title;
        this.content = content;
        this.commentsEnabled = commentsEnabled;
        this.status = PostStatus.ACTIVE;
    }

    public void update(String title, String content, boolean commentsEnabled) {
        this.title = title;
        this.content = content;
        this.commentsEnabled = commentsEnabled;
    }

    public void delete() {
        this.status = PostStatus.DELETED;
    }

}
