package com.pigeonkim.board.service;

import com.pigeonkim.board.component.MemberFinder;
import com.pigeonkim.board.exception.ForbiddenException;
import com.pigeonkim.board.exception.NotFoundException;
import com.pigeonkim.board.domain.*;
import com.pigeonkim.board.domain.entity.*;
import com.pigeonkim.board.repository.*;
import com.pigeonkim.board.web.dto.PostRequest;
import com.pigeonkim.board.web.dto.PostResponse;
import com.pigeonkim.board.domain.entity.Member;
import com.pigeonkim.board.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final MemberFinder memberFinder;

    @Transactional(readOnly = true)
    public Page<PostResponse> getPosts(Pageable pageable, String email) {
        Page<Post> posts = postRepository.findActivePosts(PostStatus.ACTIVE, pageable);

        Member member = email == null ? null : memberFinder.getByEmail(email);

        return posts.map(post -> {
            long commentCount = commentRepository.countByPostIdAndStatus(post.getId(), CommentStatus.ACTIVE);
            return PostResponse.from(post, commentCount, member);
        });
    }

    @Transactional(readOnly = true)
    public PostResponse getPost(Long postId, String email) {
        Post post = postRepository.findActiveById(postId, PostStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 게시글입니다."));
        long commentCount = commentRepository.countByPostIdAndStatus(postId, CommentStatus.ACTIVE);

        Member member = email == null ? null : memberFinder.getByEmail(email);

        return PostResponse.from(post, commentCount, member);
    }

    @Transactional
    public Long createPost(String email, PostRequest request) {

        Member author = memberFinder.getByEmail(email);

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
                .orElseThrow(() -> new NotFoundException("존재하지 않는 게시글입니다."));

        Member author = memberFinder.getByEmail(email);

        if (!post.isAuthor(author)) {
            throw new ForbiddenException("작성자만 수정할 수 있습니다.");
        }

        post.update(request.getTitle(), request.getContent(), request.isCommentsEnabled());
    }

    @Transactional
    public void deletePost(String email, Long postId) {

        Post post = postRepository.findActiveById(postId, PostStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 게시글입니다."));

        Member author = memberFinder.getByEmail(email);

        if (!post.isAuthor(author)) {
            throw new ForbiddenException("작성자만 삭제할 수 있습니다.");
        }

        post.delete();
    }
}
