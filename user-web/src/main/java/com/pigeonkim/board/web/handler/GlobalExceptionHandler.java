package com.pigeonkim.board.web.handler;

import com.pigeonkim.board.exception.BusinessException;
import com.pigeonkim.board.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

/**
 * core 가 던진 업무 예외를 화면으로 바꾼다.
 *
 * core 는 ErrorCode 까지만 안다. "그게 HTTP 로 몇 번인가"와 "사람에게 뭐라고 할 것인가"는 여기서 정한다.
 */
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ErrorMessages errorMessages;

    @ExceptionHandler(BusinessException.class)
    public ModelAndView handleBusinessException(BusinessException e) {

        ModelAndView modelAndView = new ModelAndView("error/business");
        modelAndView.setStatus(toHttpStatus(e.getErrorCode()));
        modelAndView.addObject("errorMessage", errorMessages.of(e.getErrorCode()));

        return modelAndView;
    }

    /**
     * switch 문이 아니라 switch '식'이다.
     *
     * enum 의 모든 값을 덮으면 default 를 안 써도 되고, 그게 중요한 이유가 있다 —
     * ErrorCode 에 값을 하나 더 넣는 순간 여기가 컴파일 에러가 난다.
     * default 를 두면 새 코드가 조용히 그리로 흘러 엉뚱한 상태코드로 나간다.
     *
     * 오늘 계속 이야기한 "기계가 잡게 한다"가 이 자리에서도 성립한다.
     */
    private HttpStatus toHttpStatus(ErrorCode errorCode) {
        return switch (errorCode) {
            case POST_NOT_FOUND,
                 COMMENT_NOT_FOUND,
                 PROFILE_NOT_FOUND,
                 POST_COMMENT_MISMATCH -> HttpStatus.NOT_FOUND;

            case NOT_POST_AUTHOR,
                 NOT_COMMENT_AUTHOR    -> HttpStatus.FORBIDDEN;

            case EMAIL_DUPLICATED,
                 NICKNAME_DUPLICATED,
                 COMMENTS_DISABLED,
                 POST_DELETED,
                 COMMENT_DELETED       -> HttpStatus.CONFLICT;
        };
    }
}
