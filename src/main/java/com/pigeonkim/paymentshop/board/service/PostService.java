package com.pigeonkim.paymentshop.board.service;

import com.pigeonkim.paymentshop.board.domain.BoardProfile;
import com.pigeonkim.paymentshop.board.domain.Post;
import com.pigeonkim.paymentshop.board.domain.PostRepository;
import com.pigeonkim.paymentshop.board.domain.PostStatus;
import com.pigeonkim.paymentshop.board.dto.PostRequest;
import com.pigeonkim.paymentshop.board.dto.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final BoardProfileService boardProfileService;

    @Transactional(readOnly = true)
    public Page<PostResponse> getPosts(Pageable pageable) {
        Page<Post> posts = postRepository.findActivePosts(PostStatus.ACTIVE, pageable);
        return posts.map(PostResponse::from);
    }

    @Transactional(readOnly = true)
    public PostResponse getPost(Long postId) {
        Post post = postRepository.findActiveById(postId, PostStatus.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("존재 하지 않는 게시물 입니다."));
        return PostResponse.from(post);
    }

    @Transactional
    public Long createPost(String email, PostRequest request) {

        BoardProfile boardProfile = boardProfileService.requireProfile(email);

        Post post = Post.builder()
                .author(boardProfile)
                .title(request.getTitle())
                .content(request.getContent())
                .commentsEnabled(request.isCommentsEnabled())
                .build();

        postRepository.save(post);

        return post.getId();
    }

    @Transactional
    public void updatePost(String email, Long postId, PostRequest request) {

        Post post = postRepository.findActiveById(postId, PostStatus.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("게시물이 없습니다."));

        BoardProfile boardProfile = boardProfileService.requireProfile(email);

        if (!post.isAuthor(boardProfile)) {
            throw new IllegalArgumentException("작성자만 수정할 수 있습니다.");
        }

        post.update(request.getTitle(), request.getContent(), request.isCommentsEnabled());
    }

    @Transactional
    public void deletePost(String email, Long postId) {

        Post post = postRepository.findActiveById(postId, PostStatus.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("게시물이 없습니다."));

        BoardProfile boardProfile = boardProfileService.requireProfile(email);

        if (!post.isAuthor(boardProfile)) {
            throw new IllegalArgumentException("작성자만 삭제 수 있습니다.");
        }

        post.delete();
    }
}
