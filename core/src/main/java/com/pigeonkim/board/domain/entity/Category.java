package com.pigeonkim.board.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 게시판. 트리를 이루고 깊이에 제한이 없다.
 *
 * 글은 Post.category_id 로 이 트리의 어느 노드에 붙는다 (B1-4).
 *
 * 자식 목록(@OneToMany)은 두지 않는다.
 * 트리는 findAll() 로 전부 읽어 자바에서 조립할 것이므로 JPA 가 자식을 찾아줄 필요가 없다.
 * 두면 트리를 그릴 때마다 카테고리 수만큼 쿼리가 나간다.
 */
@Entity
@Table(name = "categories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "category_name", nullable = false, length = 100)
    private String categoryName;

    @Column(name = "display_order",  nullable = false)
    private int displayOrder;


    // TODO @Builder 생성자. 최상위를 만들 때 부모에 무엇이 들어가나.

    @Builder
    public Category(Long parentId, String categoryName, int displayOrder) {
        this.parentId = parentId;
        this.categoryName = categoryName;
        this.displayOrder = displayOrder;
    }
}
