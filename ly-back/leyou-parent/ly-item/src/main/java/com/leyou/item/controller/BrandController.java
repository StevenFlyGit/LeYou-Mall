package com.leyou.item.controller;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.service.BrandService;
import com.leyou.pojo.item.Brand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 创建时间：2020/12/12
 * 商品品牌控制器
 * @author wpf
 */
@RestController
public class BrandController {

    @Autowired
    BrandService brandService;

    /**
     * 分页查询品牌控制器方法
     * @param keyWord 用户输入的关键词
     * @param page 当前页码数
     * @param rows 一页显示的行数
     * @param sortBy 排序依据
     * @param isDesc 是否降序
     * @return 查询结果
     */
    @GetMapping("/brand/page")
    public ResponseEntity<PageResult<Brand>> getBrandPage(
            @RequestParam(value = "key", required = false) String keyWord,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5")Integer rows,
            @RequestParam(value = "sortBy", required = false)String sortBy,
            @RequestParam(value = "desc", required = false)Boolean isDesc
    ) {
        PageResult<Brand> brandByPage = brandService.findBrandByPage(keyWord, page, rows, sortBy, isDesc);
        return ResponseEntity.ok(brandByPage);
    }

    /**
     * 添加一个品牌
     * @param brand 需要添加的品牌信息
     * @param categoryIds 需要添加的品牌所属的类别Id
     */
    @PostMapping("/brand")
    public ResponseEntity<Void> addOneBrand(
            Brand brand,
            @RequestParam("cids") List<Long> categoryIds
    ) {
        System.out.println("brand = " + brand);

        brandService.saveOneBrand(brand, categoryIds);
//        return ResponseEntity.status(HttpStatus.CREATED).body(null);
        //上下两句等价
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 根据id查询品牌对象
     */
    @GetMapping("/brand/{id}")
    public ResponseEntity<Brand> findBrandById(@PathVariable("id") Long brandId){
        Brand brand = brandService.findBrandById(brandId);
        return ResponseEntity.ok(brand);
    }

    /**
     * 根据品牌id批量查询品牌
     */
    @GetMapping("/brand/list")
    public ResponseEntity<List<Brand>> findBrandsByIds(@RequestParam("ids") List<Long> brandIds) {
        return ResponseEntity.ok(brandService.findBrandsByIds(brandIds));
    }

}
