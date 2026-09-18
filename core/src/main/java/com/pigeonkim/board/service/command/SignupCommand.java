package com.pigeonkim.board.service.command;

import lombok.Getter;

/**
 * 회원가입에 필요한 값 묶음.
 * <p>
 * email · password · nickname 이 전부 String 이라 나열하면 순서를 틀려도 컴파일이 된다.
 * 그래서 묶는다.
 */
@Getter
public class SignupCommand {

    private String email;
    private String password;
    private String nickname;

    private SignupCommand(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }

    public static SignupCommand of(String email, String password, String nickname) {
        return new SignupCommand(email, password, nickname);
    }

    @Override
    public String toString() {
        return String.format("SignupCommand(email=%s, password=%s, nickname=%s)", email, "password",  nickname);
    }
}
