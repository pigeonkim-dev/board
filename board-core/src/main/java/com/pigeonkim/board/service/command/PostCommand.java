package com.pigeonkim.board.service.command;

import lombok.Getter;

/**
 * 글 등록·수정에 필요한 값 묶음.
 * <p>
 * 컨트롤러가 PostRequest(web) 를 받아 이것으로 바꿔 서비스에 넘긴다.
 * 여기에는 @NotBlank 같은 검증 애노테이션이 붙지 않는다 —
 * 그건 화면이 지키는 규칙이고, 서비스에 오기 전에 이미 끝나 있어야 한다.
 * <p>
 * 왜 값을 그냥 나열하지 않고 묶는가:
 * createPost(email, title, content, ...) 로 두면 String 이 셋 나란히 온다.
 * 순서를 바꿔 넣어도 컴파일러가 못 잡는다 (『이펙티브 자바』 아이템 2).
 */
@Getter
public class PostCommand {
    private String title;
    private String content;
    private boolean commentsEnabled;

    private PostCommand(String title, String content, boolean commentsEnabled) {
        this.title = title;
        this.content = content;
        this.commentsEnabled = commentsEnabled;
    }

    public static PostCommand of(String title, String content, boolean commentsEnabled) {
        return new PostCommand(title, content, commentsEnabled);
    }

    @Override
    public String toString() {
        return String.format("PostCommand(title=%s, content=%s, commentsEnabled=%s)", title, content,  commentsEnabled);
    }
}
