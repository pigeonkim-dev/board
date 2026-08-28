package com.pigeonkim.board.exception;

import org.springframework.http.HttpStatus;

/**
 * 업무 규칙 위반을 나타내는 예외들의 부모.
 * <p>
 * 왜 부모를 두는가:
 * 핸들러가 이 하나만 잡으면 자식 5종이 전부 그리로 온다.
 * 나중에 예외를 추가해도 핸들러를 안 고쳐도 된다.
 * B3 권한, B5 비밀글, B7 신고에서 예외가 더 늘어난다.
 * <p>
 * 왜 상태를 예외가 들고 있는가:
 * "이 예외는 몇 번인가"가 예외 자신의 성질이기 때문이다.
 * NotFoundException 을 열면 404 라는 사실이 거기 있어야 한다.
 * 핸들러에 흩어 두면 예외를 추가할 때 핸들러도 같이 고쳐야 한다.
 * <p>
 * 왜 abstract 인가:
 * "업무 예외"라는 개념 자체는 던질 대상이 아니다.
 * 항상 구체적인 종류(없음 / 권한없음 / 중복 …)로 던져야 한다.
 */
public abstract class BusinessException extends RuntimeException {

    private final HttpStatus status;

    protected BusinessException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return this.status;
    }
}
