package com.pigeonkim.board.web.controller;

import com.pigeonkim.board.web.dto.CommentRequest;
import com.pigeonkim.board.service.CommentService;
import com.pigeonkim.board.web.security.CurrentUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/board/posts/{postId}/comments")
    public String create(@PathVariable Long postId,
                         @RequestParam(defaultValue = "0") int page,
                         @Valid @ModelAttribute CommentRequest request,
                         BindingResult bindingResult,
                         @CurrentUser UUID publicId,
                         RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {

            String errorMessage = bindingResult.getFieldError() != null
                    ? bindingResult.getFieldError().getDefaultMessage()
                    : "입력값을 확인해주세요.";

            redirectAttributes.addFlashAttribute("commentError", errorMessage);
            redirectAttributes.addAttribute("page", page);

            return "redirect:/board/posts/" + postId;
        }

        commentService.createComment(publicId, postId, request.getContent());

        redirectAttributes.addAttribute("page", page);

        return "redirect:/board/posts/" + postId;
    }

    @PostMapping("/board/posts/{postId}/comments/{commentId}/edit")
    public String update(@PathVariable Long postId,
                         @PathVariable Long commentId,
                         @RequestParam(defaultValue = "0") int page,
                         @Valid @ModelAttribute CommentRequest request,
                         BindingResult bindingResult,
                         @CurrentUser UUID publicId,
                         RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {

            String errorMessage = bindingResult.getFieldError() != null
                    ? bindingResult.getFieldError().getDefaultMessage()
                    : "입력값을 확인해주세요.";

            redirectAttributes.addFlashAttribute("commentError", errorMessage);
            redirectAttributes.addAttribute("page", page);

            return "redirect:/board/posts/" + postId;
        }

        commentService.updateComment(publicId, postId, commentId, request.getContent());
        redirectAttributes.addAttribute("page", page);

        return "redirect:/board/posts/" + postId;
    }

    @PostMapping("/board/posts/{postId}/comments/{commentId}/delete")
    public String delete(@PathVariable Long postId,
                         @PathVariable Long commentId,
                         @RequestParam(defaultValue = "0") int page,
                         @CurrentUser UUID publicId,
                         RedirectAttributes redirectAttributes) {

        commentService.deleteComment(publicId, postId, commentId);
        redirectAttributes.addAttribute("page", page);

        return "redirect:/board/posts/" + postId;
    }
}
