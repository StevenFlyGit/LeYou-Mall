package com.leyou.pojo.order;

/**
 * 创建时间：2020/12/30
 * 订单状态枚举类
 * @author wpf
 */

public enum OrderStatusEnum {
    INIT(1, "初始化，未付款"),
    PAY_UP(2, "已付款，未发货"),
    DELIVERED(3, "已发货，未确认"),
    CONFIRMED(4, "已确认,未评价"),
    CLOSED(5, "已关闭"),
    RATED(6, "已评价，交易结束")
    ;

    private Integer value;
    private String msg;

    OrderStatusEnum(Integer value, String msg) {
        this.value = value;
        this.msg = msg;
    }

    public Integer value(){
        return this.value;
    }

    public String msg(){
        return msg;
    }
}
