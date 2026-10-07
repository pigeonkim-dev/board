package com.pigeonkim.board.web.controller;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

// 두 사이트가 같이 쓴다. 다른 것은 "어느 IdP 등록으로 로그인하나" 하나뿐이라 그 값만 yaml 에서 받는다.
@Controller
public class LoginController {

    @Value("${board.oauth2.registration-id}")
    private  String registrationId;

    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("registrationId", registrationId);

        return "login";
    }
}
