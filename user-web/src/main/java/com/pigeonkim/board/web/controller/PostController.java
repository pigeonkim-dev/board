package com.pigeonkim.board.web.controller;

import com.pigeonkim.board.exception.BusinessException;
import com.pigeonkim.board.exception.ErrorCode;
import com.pigeonkim.board.service.command.PostCommand;
import com.pigeonkim.board.service.result.CommentResult;
import com.pigeonkim.board.service.result.PostResult;
import com.pigeonkim.board.web.dto.CommentRequest;
import com.pigeonkim.board.web.dto.PostRequest;
import com.pigeonkim.board.service.CommentService;
import com.pigeonkim.board.service.PostService;
import com.pigeonkim.board.web.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

        Page<PostResult> posts = postService.getPosts(pageable, email);
        model.addAttribute("posts", posts);
        return "board/post/list";
    }

    @GetMapping("/board/posts/{id}")
    public String detail(@PathVariable Long id,
                         @RequestParam(defaultValue = "0") int page,
                         Model model,
                         @AuthenticationPrincipal CustomUserDetails user) {

        String email = user != null ? user.getUsername() : null;

        PostResult post = postService.getPost(email, id);
        List<CommentResult> comments = commentService.getComments(id, email);

        model.addAttribute("page", page);
        model.addAttribute("post", post);
        model.addAttribute("comments", comments);
        model.addAttribute("commentRequest", new CommentRequest());

        return "board/post/detail";
    }

    @PostMapping("/board/posts/new")
    public String write(@Valid @ModelAttribute PostRequest postRequest,
                        BindingResult bindingResult,
                        @AuthenticationPrincipal CustomUserDetails user) {

        if (bindingResult.hasErrors()) {
            return "board/post/write";
        }

        Long postId = postService.createPost(
                user.getEmail(), postRequest.toCommand());

        return "redirect:/board/posts/" + postId;
    }

    @GetMapping("/board/posts/{id}/edit")
    public String editForm(@PathVariable Long id,
                           @RequestParam(defaultValue = "0") int page,
                           Model model,
                           @AuthenticationPrincipal CustomUserDetails user) {

        String email = user != null ? user.getUsername() : null;

        PostResult post = postService.getPostForEdit(email, id);
        model.addAttribute("page", page);
        model.addAttribute("post", post);
        
        return "board/post/edit";
    }

    @PostMapping("/board/posts/{id}/edit")
    public String edit(@PathVariable Long id,
                       @RequestParam(defaultValue = "0") int page,
                       @Valid @ModelAttribute PostRequest postRequest,
                       BindingResult bindingResult,
                       @AuthenticationPrincipal CustomUserDetails user,
                       RedirectAttributes redirectAttributes,
                       Model model) {

        if (bindingResult.hasErrors()) {
            String email = user != null ? user.getUsername() : null;

            PostResult post = postService.getPost(email, id);
            model.addAttribute("page", page);
            model.addAttribute("post", post);
            return "board/post/edit";
        }

        postService.updatePost(user.getEmail(), id, postRequest.toCommand());

        redirectAttributes.addAttribute("page", page);
        return "redirect:/board/posts/" + id;
    }

    @PostMapping("/board/posts/{id}/delete")
    public String delete(@PathVariable Long id,
                         @RequestParam(defaultValue = "0") int page,
                         @AuthenticationPrincipal CustomUserDetails user,
                         RedirectAttributes redirectAttributes) {

        postService.deletePost(user.getEmail(), id);

        redirectAttributes.addAttribute("page", page);

        return "redirect:/board/posts";
    }

    @GetMapping("/board/posts/new")
    public String writeForm(Model model) {
        model.addAttribute("postRequest", new PostRequest());
        return "board/post/write";
    }
}
