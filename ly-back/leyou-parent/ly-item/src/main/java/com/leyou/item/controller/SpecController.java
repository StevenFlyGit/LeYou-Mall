package com.leyou.item.controller;

import com.leyou.dto.item.SpecGroupDto;
import com.leyou.item.service.SpecService;
import com.leyou.pojo.item.SpecGroup;
import com.leyou.pojo.item.SpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 创建时间：2020/12/15
 * 商品规格控制器
 * @author wpf
 */
@RestController
//@CrossOrigin //解决跨域问题，但不能与网管中的配置同时使用
public class SpecController {

    @Autowired
    SpecService specService;

    /**
     * 根据分类查询规格组
     */
    @GetMapping("/spec/groups/of/category")
    public ResponseEntity<List<SpecGroup>> getSpecGroup(@RequestParam("id") Long CategoryId) {
        return ResponseEntity.ok(specService.findSpecGroupByCategoryId(CategoryId));
    }

    /**
     * 查询规格的通用方法
     * @param groupId 规格组Id
     * @param categoryId 商品类别Id
     * @param searching 是否搜素
     * @return 查询到的结果集合
     */
    @GetMapping("/spec/params")
    public ResponseEntity<List<SpecParam>> getSpecParam(
            @RequestParam(value = "gid", required = false) Long groupId,
            @RequestParam(value = "cid", required = false) Long categoryId,
            @RequestParam(value = "searching", required = false) Boolean searching
    ) {
        return ResponseEntity.ok(specService.findSpecParam(groupId, categoryId, searching));
    }

    /**
     * 根据分类id查询规格组和组内参数
     */
    @GetMapping("/spec/of/category")
    public ResponseEntity<List<SpecGroupDto>> getSpecGroupDtoByCategoryId(@RequestParam("id") Long cid){
        return ResponseEntity.ok(specService.SpecGroupDtoByCategoryId(cid));
    }


}
