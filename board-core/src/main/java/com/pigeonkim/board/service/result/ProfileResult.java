package com.pigeonkim.board.service.result;

import com.pigeonkim.board.domain.entity.Profile;
import lombok.Getter;

/**
 * 내 정보 화면이 쓰는 응답.
 *
 * 지금은 닉네임 하나뿐이지만 imageUrl · bio · location 이 붙으면서 는다.
 * 9/2 에 "화면과 함께 붙인다"고 미뤄둔 것들이다.
 */
@Getter
public class ProfileResult {

    private String nickname;
    private String bio;

    private ProfileResult(String nickname, String bio) {
        this.nickname = nickname;
        this.bio = bio;
    }

    public static ProfileResult from(Profile profile){
        return new ProfileResult(profile.getNickname(),
                profile.getBio()
        );
    }
}
