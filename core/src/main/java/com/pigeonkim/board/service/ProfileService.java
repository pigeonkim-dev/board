package com.pigeonkim.board.service;

import com.pigeonkim.board.component.ProfileFinder;
import com.pigeonkim.board.domain.entity.Profile;
import com.pigeonkim.board.exception.BusinessException;
import com.pigeonkim.board.exception.ErrorCode;
import com.pigeonkim.board.repository.ProfileRepository;
import com.pigeonkim.board.service.command.ProfileCommand;
import com.pigeonkim.board.service.result.ProfileResult;
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
    private final ProfileFinder profileFinder;

    @Transactional(readOnly = true)
    public ProfileResult getProfileByEmail(String email) {
        return ProfileResult.from(profileFinder.findByMemberEmail(email));
    }

    @Transactional
    public void updateProfile(String email, ProfileCommand profileCommand) {

        Profile profile = profileFinder.findByMemberEmail(email);

        if (!profile.getNickname().equals(profileCommand.getNickname())) {
            if (profileRepository.existsByNickname(profileCommand.getNickname())) {
                throw new BusinessException(ErrorCode.NICKNAME_DUPLICATED);
            }
        }

        profile.updateProfile(profileCommand.getNickname(), profileCommand.getBio());
    }
}
