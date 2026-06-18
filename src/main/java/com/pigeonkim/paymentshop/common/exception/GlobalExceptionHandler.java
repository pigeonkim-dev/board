package com.pigeonkim.paymentshop.common.exception;

import com.pigeonkim.paymentshop.board.exception.NicknameRequiredException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NicknameRequiredException.class)
    public String handleNicknameRequired(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        return "redirect:/board/profile/setup?redirect="
                + (referer != null ? referer : "/");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgument(IllegalArgumentException e, Model model) {
        model.addAttribute("errorMessage", e.getMessage());
        return "error/business";
    }
}