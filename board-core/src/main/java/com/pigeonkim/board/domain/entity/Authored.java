package com.pigeonkim.board.domain.entity;

/**
 * 작성자를 갖는 엔티티가 구현하는 인터페이스.
 *
 * BaseEntity 에 author 를 올릴 수 없어서 인터페이스로 간다.
 * Member 자신이 BaseEntity 를 상속하기 때문이다 (회원이 자기 작성자를 갖게 된다).
 */
public interface Authored {

    Profile getAuthor();

    default boolean isAuthor(Profile profile) {

        if (profile == null) {
            return false;
        }

        return getAuthor().getId().equals(profile.getId());
    }
}