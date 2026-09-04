-- V7__drop_member_nickname.sql
--
-- members.nickname 을 지운다. 닉네임은 이제 profiles 가 갖는다.
--
-- V5 가 members.nickname 을 profiles.nickname 으로 복사했고,
-- V6 이 글·댓글의 작성자를 프로필로 돌렸다. 이제 members 쪽은 아무도 읽지 않는다.
--
-- UNIQUE 제약은 컬럼을 지우면 같이 사라진다. 따로 DROP CONSTRAINT 할 필요가 없다.
-- (V4 의 체크 제약은 컬럼이 남아 있어서 따로 손봐야 했다. 경우가 다르다.)
--
-- V1 → V2 → V5 → V7 을 이어 보면 이 컬럼의 일생이 그대로 읽힌다.
-- 이것이 이미 적용된 마이그레이션을 고치지 않는 이유다.

ALTER TABLE members DROP COLUMN nickname;
