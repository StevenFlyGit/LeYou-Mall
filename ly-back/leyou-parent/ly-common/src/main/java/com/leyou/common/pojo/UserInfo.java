package com.leyou.common.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建时间：2020/12/24
 * 用户授权后生成Token的对象
 * @author wpf
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {
    private Long id;//用户ID
    private String username;//用户名称
    private String role;//用户角色(该项目中可以随意赋值)
}
