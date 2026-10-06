-- V10__member_public_id.sql
--
-- 회원 식별의 기준을 email 에서 IdP 의 public_id 로 옮기는 첫걸음.
-- members 에 public_id 를 더한다. email · password 는 아직 지우지 않는다 (V11 에서 지운다).
--
-- ⚠️ 이 마이그레이션은 회원·프로필·글·댓글을 전부 지운다.
--    로컬뿐 아니라 운영 DB 에서도 다음 배포 때 똑같이 돈다.
--
-- 왜 지우는가 (2026-09-23 결정 D3)
--   지금 있는 회원은 전부 폼으로 가입한 계정이라 IdP 에 대응하는 계정이 없다.
--   임의의 UUID 를 채워 넣어도(= 백필) 그 값으로는 아무도 로그인할 수 없다.
--   그럴 바에는 비우고, IdP 로 로그인한 사람이 새로 가입하게 한다.
--
-- 덕분에 단순해지는 것
--   행이 남아 있으면 NOT NULL 을 한 번에 못 건다.
--   (컬럼 추가 → 값 채우기 → NOT NULL 조이기 세 단계가 필요하다)
--   테이블이 비어 있으면 위반할 행이 없으므로 한 문장으로 끝난다.


-- 1. 비운다. 자식부터 지운다 — FK 가 부모 행 삭제를 막기 때문이다.
--      comments → posts      (comments.post_id)
--      comments → profiles   (comments.author_id)
--      posts    → profiles   (posts.author_id)
--      profiles → members    (profiles.member_id)
DELETE FROM comments;
DELETE FROM posts;
DELETE FROM profiles;
DELETE FROM members;


-- 2. public_id 를 더한다.
--    타입 uuid 는 PostgreSQL 의 전용 타입이다. 문자열(36자)이 아니라 16바이트로 저장되고,
--    형식이 틀린 값은 DB 가 받지 않는다.
--    IdP 의 account.public_id 도 uuid 이므로 같은 타입으로 맞춘다.
ALTER TABLE members
    ADD COLUMN public_id uuid NOT NULL;

-- 3. 한 사람(IdP 계정 하나)은 board 회원 하나다.
--    UNIQUE 제약은 인덱스를 함께 만든다. public_id 로 회원을 찾는 조회가 이 인덱스를 탄다.
ALTER TABLE members
    ADD CONSTRAINT uk_members_public_id UNIQUE (public_id);
