package com.leyou.cart.controller;

import com.leyou.cart.service.CartService;
import com.leyou.pojo.cart.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 创建时间：2020/12/28
 * 购物车业务控制器
 * @author wpf
 */
@RestController
public class CartController {

    @Autowired
    CartService cartService;

    /**
     * 添加某样商品到缓存购物车中
     * @param cart 需要添加的商品购物车对象
     */
    @PostMapping("/")
    public ResponseEntity<Void> addItemToCart(@RequestBody Cart cart) {
        cartService.addItem(cart);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 获取缓存中的购物车数据
     * @return 购物车商品集合
     */
    @GetMapping("/list")
    public ResponseEntity<List<Cart>> getItemsFromCart() {
        return ResponseEntity.ok(cartService.getItemList());
    }

    /**
     * 修改某样商品的数量
     * @param skuId 需要修改商品的skuId
     * @param num 需要修改的数量值
     */
    @PutMapping("/")
    public ResponseEntity<Void> changeItemsNum(@RequestParam("skuId") Long skuId, @RequestParam("num") Integer num) {
        cartService.changeItemsNum(skuId, num);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 从缓存中删除购物车的某样商品
     * @param skuId 需要删除商品的skuId
     */
    @DeleteMapping("/")
    public ResponseEntity<Void> deleteItem(@RequestParam("skuId") Long skuId) {
        cartService.deleteItem(skuId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 在用户登录后将浏览器本地的购物车数据同步到缓存中
     * @param cartList 需要通铺的购物车列表
     */
    @PostMapping("/list")
    public ResponseEntity<Void> putLocalCartIntoServer(@RequestBody List<Cart> cartList) {
        cartService.addLocalCartList(cartList);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
