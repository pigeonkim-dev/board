package com.pigeonkim.board.common.exception;

import org.springframework.http.HttpStatus;

/**
 * 이미 있어서 만들 수 없을 때. 409 Conflict.
 * <p>
 * 쓰이는 곳:
 * "이미 사용중인 이메일입니다."
 * "이미 사용중인 닉네임입니다."
 * <p>
 * ConflictStateException 과 같은 409 지만 나눠 둔 이유:
 * 중복은 회원가입 폼의 필드 에러로 보여줄 여지가 있다.
 * B0-6 에서 "필드 에러 vs 페이지 에러"를 가를 때 이 구분이 쓰인다.
 */
public class DuplicateException extends BusinessException {

    public DuplicateException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
