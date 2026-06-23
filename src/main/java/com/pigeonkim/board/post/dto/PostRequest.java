package com.pigeonkim.board.post.dto;

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
public class PostRequest {
    @NotBlank(message = "제목을 입력하세요")
    @Size(max = 200, message = "제목은 200자 이내로 입력하세요")
    private String title;

    @NotBlank(message = "본문을 입력하세요")
    @Size(max = 10000, message = "본문은 10,000자 이내로 입력하세요")
    private String content;

    private boolean commentsEnabled;
}
