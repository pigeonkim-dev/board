-- V6__post_comment_author_to_profile.sql
--
-- posts.author_id · comments.author_id 가 가리키는 대상을
-- members 에서 profiles 로 바꾼다.
--
-- 컬럼을 새로 만들지 않고 author_id 를 그대로 재활용한다.
-- (새 컬럼을 만들어 옮기는 방식은 무중단 배포용 기법이고 지금 상황이 아니다.)
-- 순서가 정해져 있다:
--   1. FK 제약을 먼저 뗀다. 안 떼면 2번 UPDATE 가 "members 에 없는 id" 라고 거부당한다.
--   2. 회원 id 를 그 회원의 프로필 id 로 바꾼다.
--   3. FK 를 profiles 로 다시 건다.
--
-- 제약 이름은 V1 에서 Hibernate 가 만든 것을 그대로 쓴다 (V1__init.sql:161, 177).
-- 다시 걸 때는 사람이 읽을 수 있는 이름으로 바꾼다.
--
-- members.nickname 은 아직 지우지 않는다. PostResponse 가 읽는 값이라
-- 지금 지우면 화면이 깨진다. 정리는 V7 에서 한다.

-- ── posts ──────────────────────────────────────────────
ALTER TABLE posts DROP CONSTRAINT fki5bu1f3ok7idxb9a83e2ebru8;

UPDATE posts p
SET author_id = (SELECT id FROM profiles WHERE member_id = p.author_id);

ALTER TABLE posts
    ADD CONSTRAINT fk_posts_author FOREIGN KEY (author_id) REFERENCES profiles(id);


-- ── comments ───────────────────────────────────────────
ALTER TABLE comments DROP CONSTRAINT fk1fyfbq4q0rlrs4q7mnxu2l3fl;

UPDATE comments c
SET author_id = (SELECT id FROM profiles WHERE member_id = c.author_id);

ALTER TABLE comments
    ADD CONSTRAINT fk_comments_author FOREIGN KEY (author_id) REFERENCES profiles(id);
