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
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final ErrorMessages errorMessages;
    private final LoginRefresher loginRefresher;
    private final RequestCache requestCache;

    // TODO 손질-4  두 메서드의 @AuthenticationPrincipal 을 @AuthenticationPrincipal(errorOnInvalidType = true) 로.
    //   기본값 false 는 principal 이 BoardOidcUser 가 아니면 조용히 null 을 넣는다 → 다음 줄 user.isRegistered() 에서 NPE.
    //   true 면 그 자리에서 "타입이 다르다" 는 예외가 나서 원인이 보인다. (10/4 에 실제로 겪은 silent null)
    @GetMapping("/signup")
    public String signupForm(@AuthenticationPrincipal(errorOnInvalidType = true) BoardOidcUser user,
                             Model model) {

        if (user.isRegistered()) {
            return "redirect:/";
        }

        model.addAttribute("profileSetupRequest", new ProfileSetupRequest());

        return "signup";
    }


    @PostMapping("/signup")
    public String signup(@AuthenticationPrincipal(errorOnInvalidType = true) BoardOidcUser user,
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

        SavedRequest savedRequest = requestCache.getRequest(request, response);

        if (savedRequest != null) {
            requestCache.removeRequest(request, response);
            return "redirect:" + savedRequest.getRedirectUrl();
        }

        return "redirect:/";
    }
}
