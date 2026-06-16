package com.pigeonkim.paymentshop.board.api;

import com.pigeonkim.paymentshop.board.dto.PostRequest;
import com.pigeonkim.paymentshop.board.dto.PostResponse;
import com.pigeonkim.paymentshop.board.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.validation.BindingResult;

@Controller
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @GetMapping("/board/posts")
    public String list(@PageableDefault(size = 10) Pageable pageable, Model model) {
        Page<PostResponse> posts = postService.getPosts(pageable);
        model.addAttribute("posts", posts);
        return "board/post/list";
    }

    @GetMapping("/board/posts/{id}")
    public String detail(@PathVariable Long id, Model model) {
        PostResponse post = postService.getPost(id);
        model.addAttribute("post", post);
        return "board/post/detail";
    }

    @PostMapping("/board/posts/new")
    public String write(@Valid @ModelAttribute PostRequest request,
                        BindingResult bindingResult,
                        @AuthenticationPrincipal User user) {

        if (bindingResult.hasErrors()) {
            return "board/post/write";
        }

        Long postId = postService.createPost(user.getUsername(), request);
        return "redirect:/board/posts/" + postId;
    }

    @GetMapping("/board/posts/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        PostResponse post = postService.getPost(id);
        model.addAttribute("post", post);
        return "board/post/edit";
    }

    @PostMapping("/board/posts/{id}/edit")
    public String edit(@PathVariable Long id,
                       @Valid @ModelAttribute PostRequest request,
                       BindingResult bindingResult,
                       @AuthenticationPrincipal User user,
                       Model model) {

        if (bindingResult.hasErrors()) {
            PostResponse post = postService.getPost(id);
            model.addAttribute("post", post);
            return "board/post/edit";
        }

        postService.updatePost(user.getUsername(), id, request);
        return "redirect:/board/posts/" + id;
    }

    @PostMapping("/board/posts/{id}/delete")
    public String delete(@PathVariable Long id,
                         @AuthenticationPrincipal User user) {
        postService.deletePost(user.getUsername(), id);
        return "redirect:/board/posts";
    }

    @GetMapping("/board/posts/new")
    public String writeForm(Model model) {
        model.addAttribute("postRequest", new PostRequest());
        return "board/post/write";
    }
}
