package com.pigeonkim.board.exception;

import org.springframework.http.HttpStatus;

/**
 * 대상의 상태가 이 작업을 허용하지 않을 때. 409 Conflict.
 * <p>
 * 쓰이는 곳:
 * "이 게시글은 댓글을 받지 않습니다."
 * "삭제된 댓글입니다."
 * "삭제된 게시글의 댓글은 수정할 수 없습니다."
 * <p>
 * 요청이 잘못된 게 아니라 대상이 지금 그럴 수 있는 상태가 아니다.
 * B5 비밀글, B7 신고 차단에서 더 늘어난다.
 */
public class ConflictStateException extends BusinessException {

    public ConflictStateException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
