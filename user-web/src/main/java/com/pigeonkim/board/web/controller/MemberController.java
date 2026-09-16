package com.pigeonkim.board.web.controller;


import com.pigeonkim.board.exception.BusinessException;
import com.pigeonkim.board.exception.ErrorCode;
import com.pigeonkim.board.web.handler.ErrorMessages;
import com.pigeonkim.board.web.dto.SignupRequest;
import com.pigeonkim.board.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final ErrorMessages errorMessages;

    @GetMapping("/member/signup")
    public String signupForm(Model model) {
        model.addAttribute("signupRequest", new SignupRequest());
        return "member/signup";
    }

    @GetMapping("/member/login")
    public String loginForm() {
        return "member/login";
    }

    @PostMapping("/member/signup")
    public String signup(@Valid @ModelAttribute SignupRequest signupRequest,
                         BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "member/signup";
        }

        try {

            memberService.signup(signupRequest.toCommand());

        } catch (BusinessException e) {

            // 폼에서 다룰 수 있는 것만 여기서 잡는다.
            // 나머지(회원이 없다 등)는 폼 문제가 아니므로 다시 던져 에러 화면으로 보낸다.
            // 안 그러면 엉뚱한 예외가 "중복입니다"로 둔갑한다.
            if (e.getErrorCode() != ErrorCode.EMAIL_DUPLICATED
                    && e.getErrorCode() != ErrorCode.NICKNAME_DUPLICATED) {
                throw e;
            }

            bindingResult.reject("duplicate", errorMessages.of(e.getErrorCode()));

            return "member/signup";
        }

        return "redirect:/member/login";
    }
}
