package com.pigeonkim.board.post.api;

import com.pigeonkim.board.post.dto.CommentRequest;
import com.pigeonkim.board.post.dto.CommentResponse;
import com.pigeonkim.board.post.dto.PostRequest;
import com.pigeonkim.board.post.dto.PostResponse;
import com.pigeonkim.board.post.service.CommentService;
import com.pigeonkim.board.post.service.PostService;
import com.pigeonkim.board.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.validation.BindingResult;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final CommentService commentService;  // ← 추가

    @GetMapping("/board/posts")
    public String list(@PageableDefault(size = 10) Pageable pageable,
                       Model model,
                       @AuthenticationPrincipal CustomUserDetails user) {

        String email = user != null ? user.getUsername() : null;

        Page<PostResponse> posts = postService.getPosts(pageable, email);
        model.addAttribute("posts", posts);
        return "board/post/list";
    }

    @GetMapping("/board/posts/{id}")
    public String detail(@PathVariable Long id,
                         Model model,
                         @AuthenticationPrincipal CustomUserDetails user) {

        String email = user != null ? user.getUsername() : null;

        PostResponse post = postService.getPost(id, email);
        List<CommentResponse> comments = commentService.getComments(id, email);

        model.addAttribute("post", post);
        model.addAttribute("comments", comments);
        model.addAttribute("commentRequest", new CommentRequest());

        return "board/post/detail";
    }

    @PostMapping("/board/posts/new")
    public String write(@Valid @ModelAttribute PostRequest request,
                        BindingResult bindingResult,
                        @AuthenticationPrincipal CustomUserDetails user) {

        if (bindingResult.hasErrors()) {
            return "board/post/write";
        }

        Long postId = postService.createPost(user.getEmail(), request);

        return "redirect:/board/posts/" + postId;
    }

    @GetMapping("/board/posts/{id}/edit")
    public String editForm(@PathVariable Long id,
                           Model model,
                           @AuthenticationPrincipal CustomUserDetails user) {

        String email = user != null ? user.getUsername() : null;

        PostResponse post = postService.getPost(id, email);
        model.addAttribute("post", post);
        return "board/post/edit";
    }

    @PostMapping("/board/posts/{id}/edit")
    public String edit(@PathVariable Long id,
                       @Valid @ModelAttribute PostRequest request,
                       BindingResult bindingResult,
                       @AuthenticationPrincipal CustomUserDetails user,
                       Model model) {

        if (bindingResult.hasErrors()) {
            String email = user != null ? user.getUsername() : null;

            PostResponse post = postService.getPost(id, email);
            model.addAttribute("post", post);
            return "board/post/edit";
        }

        postService.updatePost(user.getEmail(), id, request);
        return "redirect:/board/posts/" + id;
    }

    @PostMapping("/board/posts/{id}/delete")
    public String delete(@PathVariable Long id,
                         @AuthenticationPrincipal CustomUserDetails user) {

        postService.deletePost(user.getEmail(), id);
        return "redirect:/board/posts";
    }

    @GetMapping("/board/posts/new")
    public String writeForm(Model model) {
        model.addAttribute("postRequest", new PostRequest());
        return "board/post/write";
    }
}
