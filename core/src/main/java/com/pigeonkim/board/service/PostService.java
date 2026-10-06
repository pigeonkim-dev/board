package com.pigeonkim.board.service;

import com.pigeonkim.board.component.ProfileFinder;
import com.pigeonkim.board.exception.BusinessException;
import com.pigeonkim.board.exception.ErrorCode;
import com.pigeonkim.board.domain.*;
import com.pigeonkim.board.domain.entity.*;
import com.pigeonkim.board.repository.*;
import com.pigeonkim.board.service.command.PostCommand;
import com.pigeonkim.board.service.result.PostResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ProfileFinder profileFinder;

    private Post findOwnedPost(UUID publicId, Long postId) {

        Post post = postRepository.findActiveById(postId, PostStatus.ACTIVE)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        Profile profile = profileFinder.findByMemberPublicId(publicId);

        if (!post.isAuthor(profile)) {
            throw new BusinessException(ErrorCode.NOT_POST_AUTHOR);
        }

        return post;
    }

    @Transactional(readOnly = true)
    public Page<PostResult> getPosts(Pageable pageable, UUID publicId) {
        Page<Post> posts = postRepository.findActivePosts(PostStatus.ACTIVE, pageable);

        Profile profile = publicId == null ? null : profileFinder.findByMemberPublicId(publicId);

        return posts.map(post -> {
            long commentCount = commentRepository.countByPostIdAndStatus(post.getId(), CommentStatus.ACTIVE);
            return PostResult.from(post, commentCount, profile);
        });
    }

    @Transactional(readOnly = true)
    public PostResult getPost(UUID publicId, Long postId) {
        Post post = postRepository.findActiveById(postId, PostStatus.ACTIVE)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
        long commentCount = commentRepository.countByPostIdAndStatus(postId, CommentStatus.ACTIVE);

        Profile profile = publicId == null ? null : profileFinder.findByMemberPublicId(publicId);

        return PostResult.from(post, commentCount, profile);
    }

    @Transactional
    public Long createPost(UUID publicId, PostCommand postCommand) {

        Profile profile = profileFinder.findByMemberPublicId(publicId);

        Post post = Post.builder()
                .author(profile)
                .title(postCommand.getTitle())
                .content(postCommand.getContent())
                .commentsEnabled(postCommand.isCommentsEnabled())
                .build();

        postRepository.save(post);

        return post.getId();
    }

    @Transactional
    public void updatePost(UUID publicId, Long postId, PostCommand postCommand) {

        Post post = findOwnedPost(publicId, postId);

        post.update(postCommand.getTitle(), postCommand.getContent(), postCommand.isCommentsEnabled());
    }

    @Transactional
    public void deletePost(UUID publicId, Long postId) {

        Post post = findOwnedPost(publicId, postId);

        post.delete();
    }

    @Transactional(readOnly = true)
    public PostResult getPostForEdit(UUID publicId, Long postId) {

        Post post = findOwnedPost(publicId, postId);

        long commentCount = commentRepository.countByPostIdAndStatus(postId, CommentStatus.ACTIVE);

        return PostResult.from(post, commentCount, post.getAuthor());
    }
}
