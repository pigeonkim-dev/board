package com.pigeonkim.board.post.api;

import com.pigeonkim.board.post.dto.CommentRequest;
import com.pigeonkim.board.post.service.CommentService;
import com.pigeonkim.board.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/board/posts/{postId}/comments")
    public String create(@PathVariable Long postId,
                         @Valid @ModelAttribute CommentRequest request,
                         BindingResult bindingResult,
                         @AuthenticationPrincipal CustomUserDetails user,
                         RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {

            String errorMessage = bindingResult.getFieldError() != null
                    ? bindingResult.getFieldError().getDefaultMessage()
                    : "입력값을 확인해주세요.";

            redirectAttributes.addFlashAttribute("commentError", errorMessage);

            return "redirect:/board/posts/" + postId;
        }

        commentService.createComment(user.getEmail(), postId, request);

        return "redirect:/board/posts/" + postId;
    }

    @PostMapping("/board/posts/{postId}/comments/{commentId}/edit")
    public String update(@PathVariable Long postId,
                         @PathVariable Long commentId,
                         @Valid @ModelAttribute CommentRequest request,
                         BindingResult bindingResult,
                         @AuthenticationPrincipal CustomUserDetails user,
                         RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {

            String errorMessage = bindingResult.getFieldError() != null
                    ? bindingResult.getFieldError().getDefaultMessage()
                    : "입력값을 확인해주세요.";

            redirectAttributes.addFlashAttribute("commentError", errorMessage);

            return "redirect:/board/posts/" + postId;
        }

        commentService.updateComment(user.getEmail(), postId, commentId, request);

        return "redirect:/board/posts/" + postId;
    }

    @PostMapping("/board/posts/{postId}/comments/{commentId}/delete")
    public String delete(@PathVariable Long postId,
                         @PathVariable Long commentId,
                         @AuthenticationPrincipal CustomUserDetails user) {

        commentService.deleteComment(user.getEmail(), postId, commentId);

        return "redirect:/board/posts/" + postId;
    }
}
