package com.leyou.dto.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建时间：2020/12/30
 * 用于购物车提交订单时接受参数
 * @author wpf
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartDto {
    private Long skuId;// 商品skuId
    private Integer num;// 购买数量
}
