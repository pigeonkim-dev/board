package com.pigeonkim.board.common.exception;

import org.springframework.http.HttpStatus;

/**
 * 요청 자체가 말이 안 될 때. 400 Bad Request.
 * <p>
 * 지금 코드에는 해당하는 자리가 거의 없다.
 * B3 글자수 제한, B6 확장자 제한 같은 데서 쓰게 된다.
 * <p>
 * ConflictState 와 구분하는 기준:
 * 요청 값이 잘못됐으면 400,
 * 요청은 멀쩡한데 대상의 상태가 그 작업을 못 받으면 409.
 */
public class InvalidRequestException extends BusinessException {

    public InvalidRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
