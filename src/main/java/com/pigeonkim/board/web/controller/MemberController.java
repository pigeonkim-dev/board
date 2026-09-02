package com.pigeonkim.board.web.controller;


import com.pigeonkim.board.exception.DuplicateException;
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
            memberService.signup(signupRequest);
        } catch (DuplicateException e) {
            bindingResult.reject("duplicate", e.getMessage());
            return "member/signup";
        }

        return "redirect:/member/login";
    }
}
