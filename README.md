# Board

Spring Boot로 만든 게시판 서비스입니다. 회원 가입·인증, 게시글 CRUD, 댓글 기능을 갖췄고,
단순히 "동작하는 CRUD"를 작성하는데 초점을 뒀습니다.

## 기능
- 회원가입 / 로그인 — Spring Security 세션 기반
- 게시글 CRUD (작성·조회·수정·삭제) + 소프트 삭제(Delete 플래그)
- 댓글 작성·수정·삭제, 게시글별 댓글 허용 설정 + 소프트 삭제(Delete 플래그)
- 게시글 목록 페이지네이션
- Bootstrap 5 반응형 UI (공통 레이아웃, 로그인 상태별 네비게이션)

## 기술 스택
- Java 17 · Spring Boot · Spring MVC
- Spring Security (CustomUserDetails) · Spring Data JPA
- PostgreSQL · Thymeleaf · Bootstrap 5
- JUnit 5 · Mockito (서비스 단위 테스트)

## 설계 결정 — 이 프로젝트로 보여주려는 것
- **소프트 삭제**: 게시글/댓글을 물리 삭제하지 않고 `PostStatus`/`CommentStatus` enum 상태로 관리해
  이력을 보존한다.
- **명시적 상태 enum**: 상태를 enum으로 표현 — 의도가 코드에 드러나고 확장이 쉽다.
- **커스텀 인증**: 기본 `User` 대신 `CustomUserDetails` / `CustomUserDetailsService` 로
  도메인에 맞는 인증 주체를 구성한다.
- **닉네임을 Member에 통합**: 별도 1:1 프로필 테이블 대신 가입 시점에 닉네임을 받아 모델을 단순화.
- **전역 예외 처리**: `@ControllerAdvice` 기반 `GlobalExceptionHandler` 로 비즈니스 예외를 일관된
  에러 화면으로 변환한다.
- **계층 분리**: Controller → Service(`@Transactional`) → Repository, 도메인은 DTO로 감싸 노출을 차단한다.
- **단위 테스트**: 서비스 계층을 Mockito로 검증한다(작성·권한·예외 경로).

## 패키지 구조
```
com.pigeonkim.board
├── member    # 회원 / 인증 (도메인·서비스·컨트롤러·DTO)
├── post      # 게시글 · 댓글
├── security  # CustomUserDetails(Service)
└── common    # 설정 · BaseEntity · 전역 예외 처리
```

## 실행 방법
PostgreSQL이 필요합니다.

1. 데이터베이스 생성
   ```sql
   CREATE DATABASE board;
   ```
2. 환경 변수 설정
   ```bash
   export DB_USERNAME=<postgres 사용자명>
   ```
3. 실행
   ```bash
   ./gradlew bootRun
   ```
   → http://localhost:8080

## 테스트
```bash
./gradlew test
```
서비스 단위 테스트(Member / Post / Comment)는 DB 없이 실행됩니다.

## 개선 예정
- 통합 테스트 (Controller · Repository 계층)
