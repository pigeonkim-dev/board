package com.pigeonkim.board.web.controller;

import com.pigeonkim.board.exception.BusinessException;
import com.pigeonkim.board.exception.ErrorCode;
import com.pigeonkim.board.service.MemberService;
import com.pigeonkim.board.web.dto.ProfileSetupRequest;
import com.pigeonkim.board.web.handler.ErrorMessages;
import com.pigeonkim.board.web.security.BoardOidcUser;
import com.pigeonkim.board.web.security.LoginRefresher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

// 회원 가입. GET /signup 폼, POST /signup 제출. 가입이 Member 를 만드는 일이라 여기 둔다.
@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final ErrorMessages errorMessages;
    private final LoginRefresher loginRefresher;

    @GetMapping("/signup")
    public String signupForm(@AuthenticationPrincipal BoardOidcUser user, Model model) {

        if (user.isRegistered()) {
            return "redirect:/";
        }

        model.addAttribute("profileSetupRequest", new ProfileSetupRequest());

        return "signup";
    }



    @PostMapping("/signup")
    public String signup(@AuthenticationPrincipal BoardOidcUser user,
                                 @Valid @ModelAttribute ProfileSetupRequest profileSetupRequest,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {

        if (user.isRegistered()) {
            return "redirect:/";
        }

        if (bindingResult.hasErrors()) {
            return "signup";
        }

        try {
            memberService.completeSignup(user.getPublicId(), profileSetupRequest.getNickname());
        } catch (BusinessException e) {
            if (e.getErrorCode() != ErrorCode.NICKNAME_DUPLICATED) {
                throw e;
            }

            bindingResult.reject("duplicate", errorMessages.of(e.getErrorCode()));

            return "signup";
        }

        loginRefresher.refresh(request, response);
        redirectAttributes.addFlashAttribute("message", "가입이 완료되었습니다.");

        return "redirect:/";
    }
}
