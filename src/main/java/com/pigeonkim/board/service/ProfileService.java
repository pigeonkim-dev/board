package com.pigeonkim.board.service;

import com.pigeonkim.board.component.ProfileFinder;
import com.pigeonkim.board.domain.entity.Profile;
import com.pigeonkim.board.repository.ProfileRepository;
import com.pigeonkim.board.web.dto.ProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 프로필 조회·수정을 담당한다.
 * <p>
 * 메서드 이름을 me 가 아니라 getByEmail 로 둔 이유:
 * "나"는 컨트롤러가 아는 개념이다(@AuthenticationPrincipal 이 확정한다).
 * 서비스는 "이 이메일의 프로필"을 줄 뿐이다.
 * B9 에서 관리자가 남의 프로필을 볼 때 같은 메서드를 부르게 되는데,
 * 그때 me(다른사람이메일) 이라고 쓰이면 이름이 거짓말을 한다.
 */
@Service
@RequiredArgsConstructor
public class ProfileService {
    private final ProfileRepository profileRepository;

    @Transactional(readOnly = true)
    public ProfileResponse getByMemberEmail(String email) {
        Profile profile = profileRepository.findByMemberEmail(email).
                orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        return ProfileResponse.from(profile);
    }
}
