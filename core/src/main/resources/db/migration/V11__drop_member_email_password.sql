-- V11__drop_member_email_password.sql
--
-- 로그인이 IdP 로 넘어갔다. board 는 이제 이메일도 비밀번호도 저장하지 않는다.
-- 회원을 찾는 열쇠는 V10 에서 더한 public_id 하나다.
--
-- 컬럼을 지우면 그 컬럼에 걸린 제약(email 의 UNIQUE)도 함께 사라진다.
-- 남아 있는 행은 그대로 둔다. 폼으로 가입했던 행은 public_id 가 임의값이라 아무도 로그인할 수 없다.

ALTER TABLE members
    DROP COLUMN email,
    DROP COLUMN password;
