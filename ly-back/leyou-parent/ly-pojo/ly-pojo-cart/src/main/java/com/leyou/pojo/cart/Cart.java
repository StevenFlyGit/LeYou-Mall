package com.leyou.pojo.cart;

import lombok.Data;

/**
 * 创建时间：2020/12/28
 * 购物车对象实体类
 * 因为不需要连接Mysql，因此没有Mybatis-plus的相关注解
 * @author wpf
 */
@Data
public class Cart {
    private Long skuId;// 商品id
    private String title;// 标题
    private String image;// 图片
    private Long price;// 加入购物车时的价格
    private Integer num;// 购买数量
    private String ownSpec;// 商品规格参数
}
