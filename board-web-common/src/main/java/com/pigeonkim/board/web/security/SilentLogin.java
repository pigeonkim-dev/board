package com.pigeonkim.board.web.security;

/**
 * 조용한 로그인(prompt=none) 에 쓰는 이름들 (2026-11-13).
 * 메인에 처음 온 익명 방문자를 IdP 로 한 번 보내 보고, IdP 에 이미 로그인돼 있으면 비밀번호 없이 board 에도 로그인시킨다.
 * 시작하는 필터는 user-web 에 있지만, 성공 처리기(SignupRedirectSuccessHandler)가 web-common 에 있어서 이름은 여기 둔다.
 */
public final class SilentLogin {

    /** 이 브라우저에서 이미 한 번 시도했다는 표시. 만료를 주지 않아 브라우저를 닫으면 사라진다 */
    public static final String TRIED_COOKIE = "BOARD_SILENT_TRIED";

    /** 지금 진행 중인 로그인이 조용한 시도라는 표시. 성공·실패 처리기가 보고 지운다 */
    public static final String IN_PROGRESS_ATTRIBUTE = SilentLogin.class.getName() + ".IN_PROGRESS";

    /** IdP 로 가는 로그인 시작 주소에 붙여, 이번 요청에 prompt=none 을 실어 보내라고 알리는 값 */
    public static final String PROMPT_PARAMETER = "prompt";
    public static final String PROMPT_NONE = "none";

    /** 조용한 시도가 끝나면 돌아갈 곳. 시도는 메인에서만 시작한다 */
    public static final String RETURN_URL = "/";

    private SilentLogin() {
    }
}
