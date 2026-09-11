package com.pigeonkim.board.service.command;

import lombok.Getter;

/**
 * 프로필 수정에 필요한 값 묶음.
 */
@Getter
public class ProfileCommand {

    private String nickname;
    private String bio;

    private ProfileCommand(String nickname,  String bio) {
        this.nickname = nickname;
        this.bio = bio;
    }
    public  static ProfileCommand of(String nickname, String bio) {
        return new ProfileCommand(nickname, bio);
    }
}
