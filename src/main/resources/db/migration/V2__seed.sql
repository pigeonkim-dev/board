-- V2__seed.sql
--
-- 개발·확인용 시드 데이터.
-- DB 를 밀어도 Flyway 가 이 파일을 다시 적용하므로 계정과 글이 되살아난다.
-- 스키마를 바꾸는 태스크가 앞으로 12개 남았고, 그때마다 회원가입을 다시
-- 하지 않으려고 만드는 것이다.
--
-- 규칙: id 를 직접 넣지 않는다.
--   identity 컬럼은 별도 시퀀스가 다음 번호를 기억한다.
--   직접 INSERT 하면 시퀀스가 안 올라가서 나중에 앱이 중복 키로 터진다.
--   그래서 FK 는 서브쿼리로 찾아 쓴다.


-- ─────────────────────────────────────────────────────────────
-- 1. 회원
-- ─────────────────────────────────────────────────────────────
-- NOT NULL 컬럼: email, name, nickname, password, role
-- UNIQUE       : email, nickname
-- CHECK        : role 은 'USER' 또는 'ADMIN' 만 허용
--
-- TODO password 에는 BCrypt 해시를 넣는다. 평문을 넣으면 로그인이 안 된다.
--      앱으로 회원가입한 뒤 아래 명령으로 뽑아 그대로 붙여넣는다.
--        psql -d board -c "SELECT email, password FROM members;"
--
-- TODO created_at / updated_at 은 nullable 이라 안 넣어도 INSERT 는 된다.
--      다만 목록이 createdAt 내림차순으로 정렬되므로, null 이면 순서가
--      뒤죽박죽으로 보인다. 넣는 편이 낫다. now() 를 쓰거나 시각을 직접 적는다.

INSERT INTO members (email, name, nickname, password, role, created_at, updated_at)
VALUES
    ('test1@test.com', '테스트1', '1테스트', '$2a$10$SARKjdV7eXiSdNQDYrZR/OFMrKQMbjQyKkXZVbD1SBCvc4d5ZiSw2', 'USER', '2026-08-28 08:06:25.111237', '2026-08-28 08:06:25.111237'),
    ('test2@test.com', '테스트2', '2테스트', '$2a$10$ug1RpP7XqWoCUodxZmnOIuXsUVbuykrWoDikr9D4kWbUUey/iyUwC', 'USER', '2026-08-28 08:06:25.111237', '2026-08-28 08:06:25.111237'),
    ('test3@test.com', '테스트3', '3테스트', '$2a$10$s5PjY9NBEkWBtfFsxWeoHeoOhlDF/G5ZuPF964h/OV3km4yR6qfvu', 'USER', '2026-08-28 08:06:25.111237', '2026-08-28 08:06:25.111237')
    ;


-- ─────────────────────────────────────────────────────────────
-- 2. 게시글
-- ─────────────────────────────────────────────────────────────
-- NOT NULL 컬럼: title, content, comments_enabled, status, author_id
-- CHECK        : status 는 'ACTIVE' 또는 'DELETED' 만 허용
--
-- TODO author_id 는 숫자를 직접 쓰지 않고 서브쿼리로 찾는다.
--        (SELECT id FROM members WHERE email = '...')
--
-- TODO 몇 개를 넣을지는 판단이다. 화면을 확인하려면 무엇이 필요한지 생각해보라.
--      목록 정렬을 보려면 여러 개가, 댓글 허용/비허용 분기를 보려면
--      comments_enabled 가 true 인 것과 false 인 것이 각각 필요하다.

INSERT INTO posts (title, content, comments_enabled, status, author_id, created_at, updated_at)
VALUES
    ('게시글1', '게시글1-내용', true, 'ACTIVE',
     (SELECT id FROM members WHERE email = 'test1@test.com'),
     '2026-08-11 08:06:25.111237', '2026-08-28 08:06:25.111237'),
    ('게시글2', '게시글2-내용', true, 'ACTIVE',
     (SELECT id FROM members WHERE email = 'test2@test.com'),
     '2026-08-12 08:06:25.111237', '2026-08-28 08:06:25.111237'),
    ('게시글3', '게시글3-내용', true, 'ACTIVE',
     (SELECT id FROM members WHERE email = 'test3@test.com'),
     '2026-08-13 08:06:25.111237', '2026-08-28 08:06:25.111237'),

    ('게시글4', '게시글4-내용', false, 'ACTIVE',
     (SELECT id FROM members WHERE email = 'test1@test.com'),
     '2026-08-14 08:06:25.111237', '2026-08-28 08:06:25.111237'),
    ('게시글5', '게시글5-내용', false, 'ACTIVE',
     (SELECT id FROM members WHERE email = 'test2@test.com'),
     '2026-08-15 08:06:25.111237', '2026-08-28 08:06:25.111237'),
    ('게시글6', '게시글6-내용', false, 'ACTIVE',
     (SELECT id FROM members WHERE email = 'test3@test.com'),
     '2026-08-16 08:06:25.111237', '2026-08-28 08:06:25.111237'),

    ('게시글7', '게시글7-내용', true, 'ACTIVE',
     (SELECT id FROM members WHERE email = 'test1@test.com'),
     '2026-08-17 08:06:25.111237', '2026-08-28 08:06:25.111237'),
    ('게시글8', '게시글8-내용', true, 'ACTIVE',
     (SELECT id FROM members WHERE email = 'test2@test.com'),
     '2026-08-18 08:06:25.111237', '2026-08-28 08:06:25.111237'),
    ('게시글9', '게시글9-내용', true, 'ACTIVE',
     (SELECT id FROM members WHERE email = 'test3@test.com'),
     '2026-08-19 08:06:25.111237', '2026-08-28 08:06:25.111237'),

    ('게시글10', '게시글10-내용', true, 'ACTIVE',
     (SELECT id FROM members WHERE email = 'test1@test.com'),
     '2026-08-20 08:06:25.111237', '2026-08-28 08:06:25.111237'),
    ('게시글11', '게시글11-내용', true, 'ACTIVE',
     (SELECT id FROM members WHERE email = 'test2@test.com'),
     '2026-08-21 08:06:25.111237', '2026-08-28 08:06:25.111237'),
    ('게시글12', '게시글12-내용', true, 'ACTIVE',
     (SELECT id FROM members WHERE email = 'test3@test.com'),
     '2026-08-22 08:06:25.111237', '2026-08-28 08:06:25.111237')
    ;


-- ─────────────────────────────────────────────────────────────
-- 3. 댓글
-- ─────────────────────────────────────────────────────────────
-- NOT NULL 컬럼: content(500자 이내), status, author_id, post_id
-- CHECK        : status 는 'ACTIVE' 또는 'DELETED' 만 허용
--
-- TODO post_id 도 서브쿼리로 찾는다. 제목으로 찾는 게 읽기 쉽다.
--        (SELECT id FROM posts WHERE title = '...')
--
-- TODO 남의 글에 내가 단 댓글, 내 글에 남이 단 댓글을 섞어두면
--      댓글 삭제·수정 버튼이 작성자에게만 보이는지 한 화면에서 확인된다.

INSERT INTO comments (content, status, author_id, post_id, created_at, updated_at)
VALUES
    ('댓글1', 'ACTIVE',
     (SELECT id FROM members WHERE email = 'test1@test.com'),
     (SELECT id FROM posts WHERE title = '게시글12'),
     '2026-08-28 08:06:25.111237', '2026-08-28 08:06:25.111237'),
    ('댓글2', 'ACTIVE',
     (SELECT id FROM members WHERE email = 'test2@test.com'),
     (SELECT id FROM posts WHERE title = '게시글12'),
     '2026-08-28 08:06:25.111237', '2026-08-28 08:06:25.111237'),
    ('댓글3', 'ACTIVE',
     (SELECT id FROM members WHERE email = 'test3@test.com'),
     (SELECT id FROM posts WHERE title = '게시글12'),
     '2026-08-28 08:06:25.111237', '2026-08-28 08:06:25.111237'),

    ('댓글4', 'ACTIVE',
     (SELECT id FROM members WHERE email = 'test1@test.com'),
     (SELECT id FROM posts WHERE title = '게시글11'),
     '2026-08-28 08:06:25.111237', '2026-08-28 08:06:25.111237'),
    ('댓글5', 'ACTIVE',
     (SELECT id FROM members WHERE email = 'test2@test.com'),
     (SELECT id FROM posts WHERE title = '게시글11'),
     '2026-08-28 08:06:25.111237', '2026-08-28 08:06:25.111237'),
    ('댓글6', 'ACTIVE',
     (SELECT id FROM members WHERE email = 'test3@test.com'),
     (SELECT id FROM posts WHERE title = '게시글11'),
     '2026-08-28 08:06:25.111237', '2026-08-28 08:06:25.111237'),

    ('댓글7', 'ACTIVE',
     (SELECT id FROM members WHERE email = 'test1@test.com'),
     (SELECT id FROM posts WHERE title = '게시글10'),
     '2026-08-28 08:06:25.111237', '2026-08-28 08:06:25.111237'),
    ('댓글8', 'ACTIVE',
     (SELECT id FROM members WHERE email = 'test2@test.com'),
     (SELECT id FROM posts WHERE title = '게시글10'),
     '2026-08-28 08:06:25.111237', '2026-08-28 08:06:25.111237'),
    ('댓글9', 'ACTIVE',
     (SELECT id FROM members WHERE email = 'test3@test.com'),
     (SELECT id FROM posts WHERE title = '게시글10'),
     '2026-08-28 08:06:25.111237', '2026-08-28 08:06:25.111237')
    ;
