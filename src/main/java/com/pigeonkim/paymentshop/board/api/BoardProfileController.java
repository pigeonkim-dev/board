package com.pigeonkim.paymentshop.board.api;

import com.pigeonkim.paymentshop.board.domain.BoardProfile;
import com.pigeonkim.paymentshop.board.dto.BoardProfileRequest;
import com.pigeonkim.paymentshop.board.dto.PostRequest;
import com.pigeonkim.paymentshop.board.dto.PostResponse;
import com.pigeonkim.paymentshop.board.service.BoardProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class BoardProfileController {
    private final BoardProfileService boardProfileService;

    @GetMapping("/board/profile/setup")
    public String setupForm(@RequestParam(defaultValue = "/") String redirect, Model model) {

        model.addAttribute("boardProfileRequest", new BoardProfileRequest());
        model.addAttribute("redirect", redirect);

        return "board/profile/setup";
    }

    @PostMapping("/board/profile/setup")
    public String setup(@Valid @ModelAttribute BoardProfileRequest request,
                        BindingResult bindingResult,
                        @AuthenticationPrincipal User user,
                        @RequestParam(defaultValue = "/") String redirect) {
        if (bindingResult.hasErrors()) {
            return "board/profile/setup";
        }
        boardProfileService.createProfile(user.getUsername(), request);
        return "redirect:" + redirect;
    }

    @GetMapping("/board/profile/edit")
    public String editForm(@AuthenticationPrincipal User user, Model model) {

        BoardProfile profile = boardProfileService.getProfile(user.getUsername());
        if (profile == null) {
            return "redirect:/board/profile/setup";
        }

        BoardProfileRequest request = new BoardProfileRequest();
        request.setNickname(profile.getNickname());
        model.addAttribute("boardProfileRequest", request);

        return "board/profile/edit";
    }

    @PostMapping("/board/profile/edit")
    public String edit(@Valid @ModelAttribute BoardProfileRequest request,
                       BindingResult bindingResult,
                       @AuthenticationPrincipal User user) {
        if (bindingResult.hasErrors()) {
            return "board/profile/edit";
        }
        boardProfileService.updateProfile(user.getUsername(), request);
        return "redirect:/";
    }
}
