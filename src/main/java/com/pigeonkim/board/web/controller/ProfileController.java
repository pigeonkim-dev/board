package com.pigeonkim.board.web.controller;

import com.pigeonkim.board.service.ProfileService;
import com.pigeonkim.board.web.dto.ProfileResponse;
import com.pigeonkim.board.web.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 프로필 화면.
 *
 * MemberController 와 나눈 이유: 그쪽은 가입·로그인, 즉 "인증"을 다룬다.
 * 여기는 "이 사람이 어떻게 보이는가"를 다루고, 앞으로 수정 화면도 붙는다.
 * Member 와 Profile 을 나눈 것과 같은 경계다.
 *
 * 뷰는 templates/profile/me.html 이고, 모델에서 profile 이라는 이름으로 꺼내 쓴다.
 */
@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    /*
     * TODO 표 1번 칸에 적은 엔드포인트를 만든다.
     *
     *   생각할 것
     *   - 요청에서 받을 것은 없다고 표 2번에 적었다. 그럼 "누구인지"는 무엇으로 아나.
     *   - 여기는 SecurityConfig 가 로그인을 요구하는 경로다.
     *     게시글 조회에서 쓰던 `user != null ?` 방어가 여기서도 필요한가. 왜 그런가.
     *   - 화면에 값을 넘기는 방법과, 어떤 뷰를 그릴지 알리는 방법.
     *     PostController.detail 이 그 둘을 어떻게 하고 있나.
     */

    @GetMapping("/me")
    public String me(@AuthenticationPrincipal CustomUserDetails customUserDetails, Model model) {

        ProfileResponse profile = profileService.getByMemberEmail(customUserDetails.getUsername());
        model.addAttribute("profile", profile);

        return "profile/me";
    }
}
