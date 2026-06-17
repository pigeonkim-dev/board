package com.pigeonkim.paymentshop.board.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardProfileRequest {

    @NotBlank(message = "닉네임을 입력하세요")
    @Size(min = 2, max = 20, message = "닉네임은 2~20자로 입력하세요")
    private String nickname;
}
