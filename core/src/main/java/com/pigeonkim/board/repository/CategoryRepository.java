package com.pigeonkim.board.repository;

import com.pigeonkim.board.domain.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 트리를 조립하려면 전부 한 번에 읽어야 한다.
 * JpaRepository 가 주는 findAll() 이 그 일을 한다.
 *
 * 다만 findAll() 은 순서를 보장하지 않는다.
 * 트리를 제 순서로 그리려면 무엇으로 정렬해야 하는지 생각해 보라 —
 * display_order 하나로 충분한가, 아니면 하나가 더 필요한가.
 * (같은 부모 아래에서만 순서가 의미 있다는 점이 단서다)
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
