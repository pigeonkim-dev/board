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

    @NotBlank
    @Size(min = 2, max = 20)
    private String nickname;
}
