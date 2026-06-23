package com.pigeonkim.paymentshop.board.service;

import com.pigeonkim.paymentshop.board.domain.*;
import com.pigeonkim.paymentshop.board.dto.PostRequest;
import com.pigeonkim.paymentshop.board.dto.PostResponse;
import com.pigeonkim.paymentshop.member.domain.Member;
import com.pigeonkim.paymentshop.member.domain.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;

    private Member getMember(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
    }

    @Transactional(readOnly = true)
    public Page<PostResponse> getPosts(Pageable pageable) {
        Page<Post> posts = postRepository.findActivePosts(PostStatus.ACTIVE, pageable);
        return posts.map(post -> {
            long commentCount = commentRepository.countByPostIdAndStatus(post.getId(), CommentStatus.ACTIVE);
            return PostResponse.from(post, commentCount);
        });
    }

    @Transactional(readOnly = true)
    public PostResponse getPost(Long postId) {
        Post post = postRepository.findActiveById(postId, PostStatus.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시물입니다."));
        long commentCount = commentRepository.countByPostIdAndStatus(postId, CommentStatus.ACTIVE);
        return PostResponse.from(post, commentCount);
    }

    @Transactional
    public Long createPost(String email, PostRequest request) {

        Member author = getMember(email);

        Post post = Post.builder()
                .author(author)
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

        Member author = getMember(email);

        if (!post.isAuthor(author)) {
            throw new IllegalArgumentException("작성자만 수정할 수 있습니다.");
        }

        post.update(request.getTitle(), request.getContent(), request.isCommentsEnabled());
    }

    @Transactional
    public void deletePost(String email, Long postId) {

        Post post = postRepository.findActiveById(postId, PostStatus.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("게시물이 없습니다."));

        Member author = getMember(email);

        if (!post.isAuthor(author)) {
            throw new IllegalArgumentException("작성자만 삭제 수 있습니다.");
        }

        post.delete();
    }
}
