package com.leyou.cart.service;

import com.leyou.common.pojo.UserInfo;
import com.leyou.common.utils.JsonUtils;
import com.leyou.common.utils.UserHolder;
import com.leyou.pojo.cart.Cart;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 创建时间：2020/12/28
 * 处理购物车的相关业务
 * @author wpf
 */
@Service
@Slf4j
public class CartService {

    @Autowired
    StringRedisTemplate redisTemplate;

    /**
     * 添加某样商品到缓存购物车中
     * @param cart 需要添加的商品购物车对象
     */
    public void addItem(Cart cart) {
        //获取登录用户的购物车数据
        BoundHashOperations<String, String, String> redisCartByUser = getRedisCartByUser();
        //判断购物车中是否包含当前添加的商品
        if (redisCartByUser.hasKey(cart.getSkuId().toString())) {
            //若存在则添加数量()
            cart.setNum(JsonUtils.toBean(redisCartByUser.get(cart.getSkuId().toString()).toString(), Cart.class).getNum()
                    + cart.getNum());
        }
        //将数据更新到绑定对象中
        redisCartByUser.put(cart.getSkuId().toString(), JsonUtils.toString(cart));
        //记录日志
        log.debug("更新数据的用户Id为：" + redisCartByUser.getKey());
    }

    private BoundHashOperations<String, String, String> getRedisCartByUser() {
        //获取用户Id
        UserInfo user = UserHolder.getUser();
        //绑定Redis对象
        BoundHashOperations<String, String, String> boundHashOperations = redisTemplate.boundHashOps(user.getId().toString());
        return boundHashOperations;
    }

    /**
     * 获取缓存中的购物车数据
     * @return 购物车商品集合
     */
    public List<Cart> getItemList() {
        //获取用户绑定的购物车对象
        BoundHashOperations<String, String, String> redisCartByUser = getRedisCartByUser();
        //取出绑定map中的value
        List<String> values = redisCartByUser.values();
        //使用流式编程，将Json字符串转为对象
        List<Cart> cartList = values.stream().map(cartJson -> JsonUtils.toBean(cartJson, Cart.class)).collect(Collectors.toList());

        return cartList;
    }

    /**
     * 修改某样商品的数量
     * @param skuId 需要修改商品的skuId
     * @param num 需要修改的数量值
     */
    public void changeItemsNum(Long skuId, Integer num) {
        BoundHashOperations<String, String, String> redisCartByUser = getRedisCartByUser();
        //判断需要修改的Id是否在map中
        if (redisCartByUser.hasKey(skuId.toString())) {
            //获取Json字符串
            String cartStr = redisCartByUser.get(skuId.toString());
            //转为对象
            Cart cart = JsonUtils.toBean(cartStr, Cart.class);
            //修改数量
            cart.setNum(num);
            //转为字符串
            String newCartStr = JsonUtils.toString(cart);
            //重新设置回map中
            redisCartByUser.put(skuId.toString(), newCartStr);
            //记录日志
            log.debug("更新数据的用户Id为：" + redisCartByUser.getKey());
        }
    }

    /**
     * 从缓存中删除购物车的某样商品
     * @param skuId 需要删除商品的skuId
     */
    public void deleteItem(Long skuId) {
        BoundHashOperations<String, String, String> redisCartByUser = getRedisCartByUser();
        //删除数据
        redisCartByUser.delete(skuId.toString());
        //记录日志
        log.debug("更新数据的用户Id为：" + redisCartByUser.getKey());
    }

    /**
     * 在用户登录后将浏览器本地的购物车数据同步到缓存中
     * @param cartList 需要通铺的购物车列表
     */
    public void addLocalCartList(List<Cart> cartList) {
        //遍历集合，调用添加单个商品的方法来添加
        cartList.forEach(this::addItem);
    }
}
