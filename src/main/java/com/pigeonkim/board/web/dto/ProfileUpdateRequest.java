package com.pigeonkim.board.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 프로필 수정 폼이 보내오는 값.
 *
 * 화면(profile/edit.html)은 이 객체를 profileUpdateRequest 라는 이름으로 꺼내 쓴다.
 * 앞으로 imageUrl · bio · location 이 붙으면 여기도 는다.
 */
@Getter
@Setter
@NoArgsConstructor
public class ProfileUpdateRequest {

    @NotBlank(message = "닉네임을 입력하세요")
    @Size(min = 2, max = 20, message = "닉네임은 2~20자로 입력하세요")
    private String nickname;
}
