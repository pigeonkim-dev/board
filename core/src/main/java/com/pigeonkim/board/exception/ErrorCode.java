package com.pigeonkim.board.exception;

/**
 * 무엇이 잘못됐는지를 도메인의 말로 적는다.
 *
 * 여기에 HttpStatus 는 오지 않는다. "404 인가"는 HTTP 의 관심사이고 core 는 HTTP 를 모른다.
 * 옮기는 일은 user-web 의 GlobalExceptionHandler 가 한다.
 * admin-web 이 생기면 같은 코드를 다르게 옮길 수도 있다 — 그게 이 분리의 목적이다.
 *
 * 이름은 "무엇이 없다 / 누가 아니다"를 적는다. NOT_FOUND 처럼 상태코드를 옮겨 적으면 방향이 틀린 것이다.
 * 메시지는 여기 두지 않는다. 지금은 예외가 그대로 들고 있고, 화면 문구로 뺄지는 나중에 정한다.
 */
public enum ErrorCode {

    // ── 없다 ──────────────────────────────────────────────
    POST_NOT_FOUND,          // PostService 3곳 · CommentService 1곳(댓글 달 글이 없음)
    COMMENT_NOT_FOUND,       // CommentService 2곳
    PROFILE_NOT_FOUND,       // ProfileFinder 1곳

    // ── 맞지 않는다 ────────────────────────────────────────
    POST_COMMENT_MISMATCH,   // CommentService 2곳. 그 댓글이 그 글의 것이 아니다

    // ── 권한이 없다 ────────────────────────────────────────
    NOT_POST_AUTHOR,         // PostService 2곳 (수정 · 삭제)
    NOT_COMMENT_AUTHOR,      // CommentService 2곳 (수정 · 삭제)

    // ── 이미 쓰이고 있다 ───────────────────────────────────
    EMAIL_DUPLICATED,        // MemberService 1곳
    NICKNAME_DUPLICATED,     // MemberService · ProfileService 2곳

    // ── 지금 그럴 수 있는 상태가 아니다 ─────────────────────
    COMMENTS_DISABLED,       // CommentService 1곳. 글이 댓글을 안 받는다
    POST_DELETED,            // CommentService 2곳. 삭제된 글의 댓글은 못 고친다
    COMMENT_DELETED          // CommentService 1곳
}
