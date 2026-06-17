package com.pigeonkim.paymentshop.board.api;

import com.pigeonkim.paymentshop.board.dto.CommentRequest;
import com.pigeonkim.paymentshop.board.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/board/posts/{postId}/comments")
    public String create(@PathVariable Long postId,
                         @Valid @ModelAttribute CommentRequest request,
                         BindingResult bindingResult,
                         @AuthenticationPrincipal User user) {
        if (bindingResult.hasErrors()) {
            return "redirect:/board/posts/" + postId;
        }

        commentService.createComment(user.getUsername(), postId, request);
        return "redirect:/board/posts/" + postId;
    }

    @PostMapping("/board/posts/{postId}/comments/{commentId}/edit")
    public String update(@PathVariable Long postId,
                         @PathVariable Long commentId,
                         @Valid @ModelAttribute CommentRequest request,
                         BindingResult bindingResult,
                         @AuthenticationPrincipal User user) {
        if (bindingResult.hasErrors()) {
            return "redirect:/board/posts/" + postId;
        }
        commentService.updateComment(user.getUsername(), commentId, request);
        return "redirect:/board/posts/" + postId;
    }

    @PostMapping("/board/posts/{postId}/comments/{commentId}/delete")
    public String delete(@PathVariable Long postId,
                         @PathVariable Long commentId,
                         @AuthenticationPrincipal User user) {
        commentService.deleteComment(user.getUsername(), commentId);
        return "redirect:/board/posts/" + postId;
    }
}
