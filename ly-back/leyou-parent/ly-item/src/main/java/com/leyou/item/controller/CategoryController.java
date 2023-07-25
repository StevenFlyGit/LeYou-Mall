package com.leyou.item.controller;

import com.leyou.item.service.CategoryService;
import com.leyou.pojo.item.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 创建时间：2020/12/12
 * 商品类别业务控制器
 * @author wpf
 */
@RestController
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 根据父级类别Id查询
     */
    @GetMapping("/category/of/parent")
    public ResponseEntity<List<Category>> findByParentId(@RequestParam("pid") Long pid) {
        return ResponseEntity.ok(categoryService.queryCategoryByParent(pid));
    }

    /**
     * 根据分类ID集合获取分类集合
     */
    @GetMapping("/category/list")
    public ResponseEntity<List<Category>> findCategoriesByIds(@RequestParam("ids") List<Long> categoryIds){
        List<Category> categories = categoryService.findCategoriesByIds(categoryIds);
        return ResponseEntity.ok(categories);
    }

}
