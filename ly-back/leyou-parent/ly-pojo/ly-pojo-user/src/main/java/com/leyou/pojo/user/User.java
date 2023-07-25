package com.leyou.pojo.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Size;
import java.util.Date;

/**
 * 创建时间：2020/12/23
 * 乐优商城登录用户的实体类
 * @author wpf
 */
@Data
@TableName("tb_user")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;
    @Size(min = 4, max = 30, message = "用户名的长度必须在3-30之间")
    private String username;
    @Length(min = 4, max = 30, message = "密码的长度必须在3-30之间")
    private String password;
    private String phone;
    private Date createTime;
    private Date updateTime;

}
