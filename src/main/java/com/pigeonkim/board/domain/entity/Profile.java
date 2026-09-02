package com.pigeonkim.board.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 화면에 보이는 정보.
 * <p>
 * Member 가 "누구인가"라면 Profile 은 "어떻게 보이는가"다.
 * 인증은 Member 를 읽고, 화면은 Profile 을 읽는다.
 * /board 와 /thread 가 같은 프로필을 공유한다.
 * <p>
 * imageUrl · bio · location 은 B0-9c 에서 화면과 함께 더한다.
 * 지금 만들면 아무도 안 읽는 컬럼이 된다 (오늘 아침 Member.name 을 지운 이유와 같다).
 */
@Entity
@Table(name = "profiles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Profile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false, unique = true)
    private Member member;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Builder
    public Profile(Member member, String nickname) {
        this.member = member;
        this.nickname = nickname;
    }
}
