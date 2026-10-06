package com.pigeonkim.board.web.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MemberController {
    @GetMapping("/member/login")
    public String loginForm() {
        return "member/login";
    }
}
