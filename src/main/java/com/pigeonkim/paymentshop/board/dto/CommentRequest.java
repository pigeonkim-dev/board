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
public class CommentRequest {
    @NotBlank(message = "댓글을 입력하세요")
    @Size(max = 500, message = "댓글은 500자 이내로 입력하세요")
    public String content;

}
