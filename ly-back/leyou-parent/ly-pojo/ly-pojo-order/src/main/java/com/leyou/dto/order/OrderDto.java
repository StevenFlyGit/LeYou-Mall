package com.leyou.dto.order;

import lombok.Data;

import java.util.List;

/**
 * 创建时间：2020/12/30
 * 用于接收查询订单的参数
 * @author wpf
 */
@Data
public class OrderDto {
    private Long addressId; // 收获人地址id
    private Integer paymentType;// 付款类型
    private List<CartDto> carts;// 订单详情
}
