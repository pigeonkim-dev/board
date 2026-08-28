package com.pigeonkim.board.common.exception;

import org.springframework.http.HttpStatus;

/**
 * 대상은 있는데 이 사람이 할 수 없을 때. 403 Forbidden.
 * <p>
 * 쓰이는 곳:
 * "작성자만 수정할 수 있습니다."
 * "작성자만 삭제할 수 있습니다."
 * "작성자가 아닙니다."
 * <p>
 * 404 와 구분하는 기준: 대상이 존재하는가.
 * 없으면 404, 있는데 자격이 없으면 403.
 */
public class ForbiddenException extends BusinessException {

    public ForbiddenException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }
}
