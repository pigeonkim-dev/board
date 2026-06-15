package com.pigeonkim.paymentshop.member.api;


import com.pigeonkim.paymentshop.member.dto.LoginRequest;
import com.pigeonkim.paymentshop.member.dto.SignupRequest;
import com.pigeonkim.paymentshop.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/member/signup")
    public String signupForm() {
        return "member/signup";
    }

    @GetMapping("/member/login")
    public String loginForm() {
        return "member/login";
    }

    @PostMapping("/member/signup")
    public String signup(@ModelAttribute SignupRequest signupRequest) {
        memberService.signup(signupRequest);
        return "redirect:/member/login";
    }

    @PostMapping("/member/login")
    public String login(@ModelAttribute LoginRequest loginRequest) {
        memberService.login(loginRequest);
        return "redirect:/";
    }
}
