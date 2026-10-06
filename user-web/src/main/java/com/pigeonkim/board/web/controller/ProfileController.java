package com.pigeonkim.board.web.controller;

import com.pigeonkim.board.exception.BusinessException;
import com.pigeonkim.board.exception.ErrorCode;
import com.pigeonkim.board.web.handler.ErrorMessages;
import com.pigeonkim.board.service.ProfileService;
import com.pigeonkim.board.service.result.ProfileResult;
import com.pigeonkim.board.web.dto.ProfileUpdateRequest;
import com.pigeonkim.board.web.security.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

/**
 * 프로필 화면.
 * <p>
 * MemberController 와 나눈 이유: 그쪽은 가입·로그인, 즉 "인증"을 다룬다.
 * 여기는 "이 사람이 어떻게 보이는가"를 다루고, 앞으로 수정 화면도 붙는다.
 * Member 와 Profile 을 나눈 것과 같은 경계다.
 * <p>
 * 뷰는 templates/profile/me.html 이고, 모델에서 profile 이라는 이름으로 꺼내 쓴다.
 */
@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    private final ErrorMessages errorMessages;
    private final LoginRefresher loginRefresher;

    @GetMapping("/profile/me")
    public String me(@CurrentUser UUID publicId,
                     Model model) {

        ProfileResult profile = profileService.getProfileByPublicId(publicId);
        model.addAttribute("profile", profile);

        return "profile/me";
    }

    @GetMapping("/profile/me/edit")
    public String edit(@CurrentUser UUID publicId,
                       Model model) {
        ProfileResult profileResult = profileService.getProfileByPublicId(publicId);
        ProfileUpdateRequest profileUpdateRequest = new ProfileUpdateRequest();
        profileUpdateRequest.setNickname(profileResult.getNickname());
        profileUpdateRequest.setBio(profileResult.getBio());

        model.addAttribute("profileUpdateRequest", profileUpdateRequest);

        return "profile/edit";
    }

    @PostMapping("/profile/me/edit")
    public String edit(@CurrentUser UUID publicId,
                       @Valid @ModelAttribute ProfileUpdateRequest profileUpdateRequest,
                       BindingResult bindingResult,
                       RedirectAttributes redirectAttributes,
                       HttpServletRequest request,
                       HttpServletResponse response) {

        if (bindingResult.hasErrors()) {
            return "profile/edit";
        }

        try {
            profileService.updateProfile(publicId, profileUpdateRequest.toCommand());

        } catch (BusinessException e) {
            if (e.getErrorCode() != ErrorCode.NICKNAME_DUPLICATED) {
                throw e;
            }

            bindingResult.reject("duplicate", errorMessages.of(e.getErrorCode()));

            return "profile/edit";
        }

        loginRefresher.refresh(request, response);

        redirectAttributes.addFlashAttribute("message", "프로필이 수정 되었습니다.");

        return "redirect:/profile/me";
    }
}
