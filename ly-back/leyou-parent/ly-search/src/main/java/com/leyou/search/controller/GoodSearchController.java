package com.leyou.search.controller;

import com.leyou.client.item.ItemClient;
import com.leyou.common.pojo.PageResult;
import com.leyou.dto.item.SpuDto;
import com.leyou.dto.search.GoodsDto;
import com.leyou.dto.search.GoodsSearchResult;
import com.leyou.pojo.item.Sku;
import com.leyou.pojo.item.SpecParam;
import com.leyou.pojo.item.SpuDetail;
import com.leyou.pojo.search.GoodSearchRequest;
import com.leyou.search.service.GoodDataSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 创建时间：2020/12/18
 * 商品搜索控制器
 * @author wpf
 */
@RestController
public class GoodSearchController {

    @Autowired
    private GoodDataSearchService goodDataSearchService;
    @Autowired
    private ItemClient itemClient;

    @GetMapping("test/getSpuMsgByPage")
    private PageResult<SpuDto> testGetSpuMsgByPage() {
        return itemClient.getSpuMsgByPage(null, null, null, null);
    }

    @GetMapping("test/getSpuDetailBySpuId")
    private SpuDetail testGetSpuDetailBySpuId() {
        return itemClient.getSpuDetailBySpuId(3L);
    }

    @GetMapping("test/getSpecParam")
    private List<SpecParam> testGetSpecParam() {
        return itemClient.getSpecParam(null, 76L, true);
    }

    @GetMapping("test/getSkusBySpuId")
    private List<Sku> testGetSkusBySpuId() {
        return itemClient.getSkusBySpuId(3L);
    }

    /**
     * 为前端搜索商品提供的接口
     * @param goodSearchRequest 接收请求参数的对象，包括关键词和当前页码
     * @return 搜索到的结果对象
     */
    @PostMapping("/page")
    private ResponseEntity<GoodsSearchResult<GoodsDto>> searchGoods(
          @RequestBody GoodSearchRequest goodSearchRequest
    ) {
        return ResponseEntity.ok(goodDataSearchService.searchGoodsByKeyWordAndPage(goodSearchRequest));
    }

    /**
     * 仅仅进行分页操作时调用的接口
     * @param goodSearchRequest 接收请求参数的对象，包括关键词和当前页码
     * @return 搜索到的结果对象
     */
    @PostMapping("/page/onlyPage")
    private ResponseEntity<List<GoodsDto>> searchGoodsOnlyForPage(
            @RequestBody GoodSearchRequest goodSearchRequest
    ) {
        return ResponseEntity.ok(goodDataSearchService.searchGoodsOnlyForPage(goodSearchRequest));
    }

}
