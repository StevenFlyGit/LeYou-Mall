package com.leyou.item.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.leyou.common.pojo.PageResult;
import com.leyou.common.utils.BeanHelper;
import com.leyou.dto.item.SpuDto;
import com.leyou.item.service.GoodsService;
import com.leyou.pojo.item.Sku;
import com.leyou.pojo.item.Spu;
import com.leyou.pojo.item.SpuDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 创建时间：2020/12/15
 * 商品数据控制器
 * @author wpf
 */
@RestController
public class GoodsController {

    @Autowired
    GoodsService goodsService;

    /**
     * 分页查询商品的Spu信息，并封装到SpuDto中返回给前端
     * @param keyWord 用户输入的关键词
     * @param page 当前页码数
     * @param rows 一页显示的行数
     * @param isSaleable 是否上架
     * @return 查询到的结果集合
     */
    @GetMapping("/spu/page")
    public ResponseEntity<PageResult<SpuDto>> getSpuMsgByPage(
            @RequestParam(value = "key", required = false) String keyWord,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5")Integer rows,
            @RequestParam(value = "saleable", required = false)Boolean isSaleable
    ) {
        //通过业务方法查询到Spu的信息
        IPage<Spu> spuIPage = goodsService.findSpuByPage(keyWord, page, rows, isSaleable);
        //封装到SpuDto对象中
        List<Spu> spuList = spuIPage.getRecords();
        //使用封装数据的工具类(是对BeanUtils的二次封装)
        List<SpuDto> spuDtoList = BeanHelper.copyWithCollection(spuList, SpuDto.class);

        //将categoryName和brandName的数据封装到SpuDto中
        List<SpuDto> spuDtoListWithName = goodsService.setCategoryNameAndBrandName(spuDtoList);

        //构建自定义的分页对象
        PageResult<SpuDto> pageResult = new PageResult<>(spuDtoListWithName, spuIPage.getTotal(), spuIPage.getPages());

        return ResponseEntity.ok(pageResult);
    }

    /**
     * 修改商品上下架，更新spu信息，同时需要更新sku
     * @param spuId spu商品的Id
     * @param isSaleable 是否上架
     * @return Void
     */
    @PutMapping("/spu/saleable") //id=1&saleable=1
    public ResponseEntity<Void> changeGoodsSalable(
            @RequestParam("id") Long spuId,
            @RequestParam("saleable") Boolean isSaleable
    ) {
        goodsService.changeGoodsState(spuId, isSaleable);
        return null;
    }


    /**
     * 根据spuId查询spu信息
     */
    @GetMapping("/sku/of/spu")
    public ResponseEntity<List<Sku>> getSkusBySpuId(@RequestParam("id") Long spuId) {
        return ResponseEntity.ok(goodsService.findSkusBySpuId(spuId));
    }

    /**
     * 根据spuId查询spuDetail
     */
    @GetMapping("/spu/detail")
    public ResponseEntity<SpuDetail> getSpuDetailBySpuId(@RequestParam("id") Long spuId) {
        return ResponseEntity.ok(goodsService.findSpuDetailBySpuId(spuId));
    }

    /**
     * 根据SpuId查询并封装SpuDto对象
     */
    @GetMapping("/spu/{id}")
    public ResponseEntity<SpuDto> getSpuDtoBySpuId(@PathVariable("id") Long spuId) {
        return ResponseEntity.ok(goodsService.findSpuDtoBySpuId(spuId));
    }

    /**
     * 根据skuId集合查询Sku对象集合
     */
    @GetMapping("/sku/list")
    public ResponseEntity<List<Sku>> getSkuListByIds(@RequestParam("ids") List<Long> skuIds) {
        return ResponseEntity.ok(goodsService.findSkuListByIds(skuIds));
    }

    /**
     * 减少商品库存
     */
    @PutMapping("/stock/minus")
    public ResponseEntity<Void> minesStock(@RequestBody Map<Long, Integer> stockParamMap) {
        goodsService.minesStock(stockParamMap);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 增加商品库存
     */
    @PutMapping("/stock/plus")
    public ResponseEntity<Void> plusStock(@RequestBody Map<Long, Integer> stockParamMap) {
        goodsService.plusStock(stockParamMap);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
