package com.leyou.client.item;

import com.leyou.common.pojo.PageResult;
import com.leyou.dto.item.SpecGroupDto;
import com.leyou.dto.item.SpuDto;
import com.leyou.pojo.item.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 创建时间：2020/12/18
 * 商品微服务向外提供的服务调用接口
 * Feign接口中声明的返回值可以不使用ResponseEntity来封装
 * @author wpf
 */
@FeignClient("item-service")
public interface ItemClient {

    /**
     * 分页查询商品的Spu信息，并封装到SpuDto中返回给前端
     */
    @GetMapping("/spu/page")
    public PageResult<SpuDto> getSpuMsgByPage(
            @RequestParam(value = "key", required = false) String keyWord,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5")Integer rows,
            @RequestParam(value = "saleable", required = false)Boolean isSaleable
    );

    /**
     * 查询规格的通用方法
     */
    @GetMapping("/spec/params")
    public List<SpecParam> getSpecParam(
            @RequestParam(value = "gid", required = false) Long groupId,
            @RequestParam(value = "cid", required = false) Long categoryId,
            @RequestParam(value = "searching", required = false) Boolean searching
    );

    /**
     * 根据spuId查询spu信息
     */
    @GetMapping("/sku/of/spu")
    public List<Sku> getSkusBySpuId(@RequestParam("id") Long spuId);

    /**
     * 根据spuId查询spuDetail
     */
    @GetMapping("/spu/detail")
    public SpuDetail getSpuDetailBySpuId(@RequestParam("id") Long spuId);

    /**
     * 根据分类ID集合获取分类集合
     */
    @GetMapping("/category/list")
    public List<Category> findCategoriesByIds(@RequestParam("ids") List<Long> categoryIds);

    /**
     * 根据品牌id批量查询品牌
     */
    @GetMapping("/brand/list")
    public List<Brand> findBrandsByIds(@RequestParam("ids") List<Long> brandIds);

    /**
     * 根据SpuId查询并封装SpuDto对象
     */
    @GetMapping("/spu/{id}")
    public SpuDto getSpuDtoBySpuId(@PathVariable("id") Long spuId);

    /**
     * 根据id查询品牌对象
     */
    @GetMapping("/brand/{id}")
    public Brand findBrandById(@PathVariable("id") Long brandId);

    /**
     * 根据分类id查询规格组和组内参数
     */
    @GetMapping("/spec/of/category")
    public List<SpecGroupDto> getSpecGroupDtoByCategoryId(@RequestParam("id") Long cid);

    /**
     * 根据skuId集合查询Sku对象集合
     */
    @GetMapping("/sku/list")
    public List<Sku> getSkuListByIds(@RequestParam("ids") List<Long> skuIds);

    /**
     * 减少商品库存
     */
    @PutMapping("/stock/minus")
    public ResponseEntity<Void> minesStock(@RequestBody Map<Long, Integer> stockParamMap);

    /**
     * 增加商品库存
     */
    @PutMapping("/stock/plus")
    public ResponseEntity<Void> plusStock(@RequestBody Map<Long, Integer> stockParamMap);
}
