package com.leyou.common.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 创建时间：2020/12/24
 * Jwt中的Token中的载荷对象
 * @author wpf
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payload<T> {

    private String id; //载荷中的Id
    private T info; //载荷中的可变对象(一般存储用户信息)
    private Date expiration; //Token失效时间

}
