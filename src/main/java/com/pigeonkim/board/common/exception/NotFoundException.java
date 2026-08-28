package com.pigeonkim.board.common.exception;

import org.springframework.http.HttpStatus;

/**
 * 찾는 대상이 없을 때. 404 Not Found.
 * <p>
 * 쓰이는 곳 (B0-5, B0-6 에서 교체):
 * "존재하지 않는 게시글입니다."
 * "존재하지 않는 회원입니다."
 * "존재하지 않는 댓글입니다."
 * "게시글과 댓글이 일치하지 않습니다."
 * ↑ 이건 400 이 아니라 404 다.
 * 다르게 응답하면 "그 댓글 ID 가 다른 글에는 존재한다"는 사실이 새어나간다.
 */
public class NotFoundException extends BusinessException {

    public NotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
