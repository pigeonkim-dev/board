package com.pigeonkim.board.web.controller;

import com.pigeonkim.board.exception.DuplicateException;
import com.pigeonkim.board.service.ProfileService;
import com.pigeonkim.board.service.result.ProfileResult;
import com.pigeonkim.board.web.dto.ProfileUpdateRequest;
import com.pigeonkim.board.web.security.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.pigeonkim.board.web.security.CustomUserDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    private final SecurityContextRepository securityContextRepository;
    private final CustomUserDetailsService customUserDetailsService;

    @GetMapping("/profile/me")
    public String me(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                     Model model) {

        ProfileResult profile = profileService.getProfileByEmail(customUserDetails.getUsername());
        model.addAttribute("profile", profile);

        return "profile/me";
    }

    @GetMapping("/profile/me/edit")
    public String edit(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                       Model model) {
        ProfileResult profileResult = profileService.getProfileByEmail(customUserDetails.getUsername());
        ProfileUpdateRequest profileUpdateRequest = new ProfileUpdateRequest();
        profileUpdateRequest.setNickname(profileResult.getNickname());

        model.addAttribute("profileUpdateRequest", profileUpdateRequest);

        return "profile/edit";
    }

    @PostMapping("/profile/me/edit")
    public String edit(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                       @Valid @ModelAttribute ProfileUpdateRequest profileUpdateRequest,
                       BindingResult bindingResult,
                       Model model,
                       RedirectAttributes redirectAttributes,
                       HttpServletRequest httpServletRequest,
                       HttpServletResponse httpServletResponse) {

        if (bindingResult.hasErrors()) {
            return "profile/edit";
        }

        try {

            profileService.updateProfile(customUserDetails.getUsername(), profileUpdateRequest.toCommand());

        } catch (DuplicateException e) {
            bindingResult.reject("duplicate", e.getMessage());
            return "profile/edit";
        }

        // ── 세션 안의 인증 정보 갱신 ────────────────────────────────
        // DB 는 바뀌었지만 세션의 CustomUserDetails 는 로그인 시점의 옛 닉네임을 들고 있다.
        // 그래서 navbar 가 옛 이름을 보여준다. 여기서 갈아끼운다.

        // 1. 바뀐 값으로 principal 을 다시 만든다.
        //    로그인할 때 쓰는 그 코드를 그대로 재사용한다 (회원 + 프로필을 DB 에서 다시 읽어 조립).
        CustomUserDetails newPrincipal = (CustomUserDetails)
                customUserDetailsService.loadUserByUsername(customUserDetails.getUsername());

        // 2. "이미 인증된 상태"의 Authentication 을 만든다.
        //    생성자가 아니라 authenticated(...) 를 쓴다. 생성자로 만들면 "아직 인증 안 됨" 상태가 된다.
        Authentication newAuthentication = UsernamePasswordAuthenticationToken.authenticated(
                newPrincipal, newPrincipal.getPassword(), newPrincipal.getAuthorities());

        // 3. 빈 SecurityContext 를 새로 만들어 담는다.
        //    지금 것을 가져다 고치지 않는 이유는 다른 요청과 같은 객체를 공유할 수 있어서다.
        SecurityContext newContext = SecurityContextHolder.createEmptyContext();
        newContext.setAuthentication(newAuthentication);

        // 4. 이번 요청이 끝날 때까지 쓸 자리에 넣는다 (스레드에 붙는 임시 보관소).
        SecurityContextHolder.setContext(newContext);

        // 5. 세션에 저장한다. Spring Security 6 부터 자동 저장이 없어져 직접 불러야 한다.
        //    이 줄을 빼면 이번 요청에서만 바뀌고 리다이렉트하면 옛 이름으로 돌아온다.
        securityContextRepository.saveContext(newContext, httpServletRequest, httpServletResponse);

        redirectAttributes.addFlashAttribute("message", "프로필이 수정 되었습니다.");

        return "redirect:/profile/me";
    }
}
