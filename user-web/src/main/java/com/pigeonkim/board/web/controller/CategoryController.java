package com.pigeonkim.board.web.controller;

import com.pigeonkim.board.domain.entity.Category;
import com.pigeonkim.board.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * 게시판 화면.
 *
 * ⚠️ 지금은 B1-1 을 눈으로 확인하려고 만든 임시 화면이다.
 *
 * 리포지토리를 컨트롤러가 직접 부르고 있는데, 이건 원래 하면 안 된다 —
 * 서비스를 건너뛰면 나중에 권한 검사나 트리 조립을 넣을 자리가 없다.
 * B1-2 에서 CategoryService 가 생기면 그쪽을 부르도록 바꾼다.
 *
 * 임시인 줄 알면서 두는 것과 모르고 두는 것은 다르다. 그래서 여기 적어둔다.
 */
@Controller
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryRepository categoryRepository;

    @GetMapping("/board")
    public String tree(Model model) {

        List<Category> categories = categoryRepository.findAll(Sort.by("displayOrder"));
        model.addAttribute("categories", categories);

        return "board/category-list";
    }
}
