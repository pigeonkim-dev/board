package com.pigeonkim.paymentshop.board.domain;

import com.pigeonkim.paymentshop.common.domain.BaseEntity;
import com.pigeonkim.paymentshop.member.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "board_profiles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardProfile extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, unique = true)
    private Member member;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Builder
    public BoardProfile(Member member, String nickname) {
        this.member = member;
        this.nickname = nickname;
    }

    public void changeNickname(String newNickname) {
        this.nickname = newNickname;
    }
}
