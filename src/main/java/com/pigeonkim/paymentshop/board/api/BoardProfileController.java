package com.pigeonkim.paymentshop.board.api;

import com.pigeonkim.paymentshop.board.domain.BoardProfile;
import com.pigeonkim.paymentshop.board.dto.BoardProfileRequest;
import com.pigeonkim.paymentshop.board.service.BoardProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;

@Controller
@RequiredArgsConstructor
public class BoardProfileController {
    private final BoardProfileService boardProfileService;

    @GetMapping("/board/profile/setup")
    public String setupForm(@AuthenticationPrincipal User user) {
        return "/board/profile/setup";
    }

    @PostMapping("/board/profile/setup")
    public String setupForm(@Valid @ModelAttribute BoardProfileRequest request,
                            @AuthenticationPrincipal User user) {
        boardProfileService.createProfile(user.getUsername(), request);

        return "redirect:/";
    }

    @GetMapping("/board/profile/edit")
    public String editForm(@AuthenticationPrincipal User user, Model model) {
        BoardProfile profile = boardProfileService.getProfile(user.getUsername());
        model.addAttribute("nickname", profile.getNickname());
        return "board/profile/edit";
    }

    @PostMapping("/board/profile/edit")
    public  String edit(@Valid @ModelAttribute BoardProfileRequest request,
                        @AuthenticationPrincipal User user){

        boardProfileService.updateProfile(user.getUsername(), request);

        return "redirect:/";
    }
}
