package com.leyou.pojo.user;

import lombok.Data;

/**
 * 创建时间：2020/12/30
 * 地址表实体类(暂时还没有建表)
 * @author wpf
 */
@Data
public class Address {
    private Long id;
    private Long userId;
    private String addressee;// 收件人姓名
    private String phone;// 电话
    private String province;// 省份
    private String city;// 城市
    private String district;// 区
    private String street;// 街道地址
    private String  postcode;// 邮编
    private Boolean isDefault;
}
