package com.pigeonkim.board.web.handler;

import com.pigeonkim.board.exception.ErrorCode;
import org.springframework.stereotype.Component;

/**
 * ErrorCode 를 사용자가 읽을 문구로 옮긴다.
 *
 * core 는 "무엇이 잘못됐나"(ErrorCode)까지만 알고, "사람에게 뭐라고 할 것인가"는 여기서 정한다.
 * 그래서 이 클래스는 user-web 에 있다. admin-web 이 생기면 같은 코드를 다르게 옮길 수 있다.
 *
 * 정적 유틸이 아니라 빈으로 둔 이유:
 * 나중에 문구를 messages.properties 로 빼게 되면 MessageSource 를 주입받아야 한다.
 * 그때 이 클래스 한 곳만 바뀌고 쓰는 쪽은 그대로다.
 */
@Component
public class ErrorMessages {

    public String of(ErrorCode errorCode) {
        return switch (errorCode) {
            case POST_NOT_FOUND        -> "존재하지 않는 게시글입니다.";
            case COMMENT_NOT_FOUND     -> "존재하지 않는 댓글입니다.";
            case PROFILE_NOT_FOUND     -> "존재하지 않는 회원입니다.";

            // 일부러 COMMENT_NOT_FOUND 와 같은 문구를 쓴다.
            // "그 댓글은 그 글의 것이 아니다"라고 알려주면 남의 댓글이 어디 붙어 있는지를
            // 흘리게 된다. 없는 것처럼 보이는 편이 낫다. (8/28 에 400 이 아니라 404 로 정한 것과 같은 이유)
            case POST_COMMENT_MISMATCH -> "존재하지 않는 댓글입니다.";

            case NOT_POST_AUTHOR,
                 NOT_COMMENT_AUTHOR    -> "작성자만 수정하거나 삭제할 수 있습니다.";

            case EMAIL_DUPLICATED      -> "이미 사용 중인 이메일입니다.";
            case NICKNAME_DUPLICATED   -> "이미 사용 중인 닉네임입니다.";

            case COMMENTS_DISABLED     -> "이 게시글은 댓글을 받지 않습니다.";
            case POST_DELETED          -> "삭제된 게시글입니다.";
            case COMMENT_DELETED       -> "삭제된 댓글입니다.";
        };
    }
}
